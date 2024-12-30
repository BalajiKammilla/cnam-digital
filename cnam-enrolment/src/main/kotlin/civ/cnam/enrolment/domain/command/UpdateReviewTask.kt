package civ.cnam.enrolment.domain.command

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.InvalidReviewTaskUpdateStatus
import civ.cnam.enrolment.domain.error.EnrolmentErrors.InvalidReviewTaskUpdateType
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ReviewTaskAlreadyCompleted
import civ.cnam.enrolment.domain.error.UpdateReviewTaskError
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.DedupeReviewTask
import civ.cnam.enrolment.domain.model.entity.IdentityDocumentReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateDedupeAttributes
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateReviewTaskStatus
import civ.cnam.enrolment.domain.model.query.enrolment.GetReviewTask
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeMatchAttributes
import civ.cnam.enrolment.domain.model.type.enrolment.alert.EnrolmentRejectedAlert
import dev.dry.alert.domain.service.AlertCreator
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.function.map
import dev.dry.common.function.mapLeft
import dev.dry.core.data.model.value.UserName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import jakarta.validation.constraints.Size
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateReviewTask(
    private val getReviewTask: GetReviewTask,
    private val updateReviewTaskStatus: UpdateReviewTaskStatus,
    private val updateDedupeAttributes: UpdateDedupeAttributes,
    private val createCorrectiveAction: CreateCorrectiveAction,
    private val alertCreator: AlertCreator,
) {
    @Transactional
    operator fun invoke(
        id: ReviewTask.ID,
        updatedBy: UserName,
        update: ReviewTaskUpdate,
    ): Either<UpdateReviewTaskError, Unit> {
        val reviewTask = getReviewTask(id)
            .fold({ return failed(it) }, { it })

        if (reviewTask.completedAt != null) {
            return failed(ReviewTaskAlreadyCompleted)
        }

        return when (update) {
            is IdentityDocumentReviewTaskUpdate -> when(reviewTask) {
                is IdentityDocumentReviewTask -> onUpdateIdentityDocumentReviewTask(reviewTask, update.status)
                else -> failed(InvalidReviewTaskUpdateType)
            }
            is DedupeReviewTaskUpdate -> when(reviewTask) {
                is DedupeReviewTask -> onUpdateDedupeReviewTask(reviewTask, update)
                else -> failed(InvalidReviewTaskUpdateType)
            }
        }.map {
            logger.info("updating review task '${id}' of type '${reviewTask.type}' with status '${update.status}'")
            updateReviewTaskStatus(reviewTask.id, updatedBy, update.status, update.note)
                .fold({ failed(it) }, { completed() })
        }
    }

    private fun onUpdateIdentityDocumentReviewTask(
        reviewTask: ReviewTask,
        status: ReviewTaskStatus,
    ): Either<UpdateReviewTaskError, Unit> {
        return when(status) {
            ReviewTaskStatus.APPROVED -> right(Unit)
            ReviewTaskStatus.REJECTED -> onIdentityDocumentReviewTaskRejected(reviewTask)
            ReviewTaskStatus.PENDING -> left(InvalidReviewTaskUpdateStatus)
        }
    }

    private fun onIdentityDocumentReviewTaskRejected(reviewTask: ReviewTask): Either<UpdateReviewTaskError, Unit> {
        return createCorrectiveAction(reviewTask.enrolment.id, CorrectiveActionType.IDENTITY_DOCUMENT)
            .mapLeft {
                logger.error(
                    "failed to create corrective action for rejected identity document " +
                            "for enrolment '${reviewTask.enrolment.enrolmentId}'"
                )
                EnrolmentErrors.FailedToCreateCorrectiveAction
            }
    }

    private fun onUpdateDedupeReviewTask(
        reviewTask: ReviewTask,
        update: DedupeReviewTaskUpdate,
    ): Either<UpdateReviewTaskError, Unit> {
        return when(update.status) {
            ReviewTaskStatus.APPROVED -> onDedupeReviewTaskApproved(reviewTask, update.dedupeAttributes)
            ReviewTaskStatus.REJECTED -> onDedupeReviewTaskRejected(reviewTask)
            ReviewTaskStatus.PENDING -> left(InvalidReviewTaskUpdateStatus)
        }
    }

    private fun onDedupeReviewTaskApproved(
        reviewTask: ReviewTask,
        dedupeAttributes: DedupeMatchAttributes?,
    ): Either<UpdateReviewTaskError, Unit> {
        return when {
            dedupeAttributes != null -> updateDedupeAttributes(reviewTask.enrolment.id, dedupeAttributes)
            else -> completed()
        }
    }

    private fun onDedupeReviewTaskRejected(reviewTask: ReviewTask): Either<UpdateReviewTaskError, Unit> {
        val alert = EnrolmentRejectedAlert.from(
            reviewTask.enrolment,
            EnrolmentRejectedAlert.EnrolmentRejectedReason.DUPLICATE_ENROLMENT_FOUND
        ).fold({ return left(EnrolmentErrors.EnrolmentDetailsNotFound) }, { it })

        return alertCreator.create(alert)
            .mapLeft {
                logger.error("failed to create enrolment rejected alert - ${reviewTask.enrolment.enrolmentId}")
                // TODO("rollback transaction")
                EnrolmentErrors.FailedToCreateAlert
            }
            .map {}
    }

    enum class ReviewTaskUpdateType { IDENTITY_DOCUMENT_REVIEW_TASK_UPDATE, DEDUPE_REVIEW_TASK_UPDATE }
    sealed interface ReviewTaskUpdate {
        val type: ReviewTaskUpdateType
        val status: ReviewTaskStatus
        @get:Size(max = 1000)
        val note: String?
    }
    interface IdentityDocumentReviewTaskUpdate : ReviewTaskUpdate {
        override val type: ReviewTaskUpdateType get() = ReviewTaskUpdateType.IDENTITY_DOCUMENT_REVIEW_TASK_UPDATE
    }
    class DedupeReviewTaskUpdate(
        override val status: ReviewTaskStatus,
        @Size(max = 1000)
        override val note: String?,
        val dedupeAttributes: DedupeMatchAttributes?,
    ) : ReviewTaskUpdate {
        override val type: ReviewTaskUpdateType = ReviewTaskUpdateType.DEDUPE_REVIEW_TASK_UPDATE
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateReviewTask::class.java)

        private fun completed(): Either.Right<Unit> {
            logger.info("updating review task completed")
            return right(Unit)
        }

        private fun failed(error: UpdateReviewTaskError): Either.Left<UpdateReviewTaskError> {
            logger.info("updating review task failed - $error")
            return left(error)
        }
    }
}