package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.UpdateEnrolmentVerifiedAt.UpdateEnrolmentToVerifiedError.UpdateEnrolmentToVerifiedFailed
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_INCOMPLETE
import civ.cnam.enrolment.domain.error.EnrolmentErrors.FailedToCreateAlert
import civ.cnam.enrolment.domain.error.EnrolmentErrors.UPDATE_ENROLMENT_TO_VERIFIED_FAILED
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import civ.cnam.enrolment.domain.model.repository.EnrolmentOutboxTaskRepository
import civ.cnam.enrolment.domain.model.type.enrolment.alert.EnrolmentVerifiedAlert
import dev.dry.alert.domain.service.AlertCreator
import dev.dry.common.error.CodedError
import dev.dry.common.error.CodedError.DefaultCodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.time.TimeProvider
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.persistence.OptimisticLockException
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateEnrolmentVerifiedAt(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val enrolmentOutboxTaskRepository: EnrolmentOutboxTaskRepository,
    private val alertCreator: AlertCreator,
) {
    @Throws(OptimisticLockException::class)
    operator fun invoke(enrolment: PartialEnrolmentEntity): Either<UpdateEnrolmentToVerifiedError, Unit> {
        logger.info("checking for update of enrolment verified status")
        if (!enrolment.verificationTasksPending &&
            !enrolment.reviewTasksPending &&
            !enrolment.correctiveActionsPending
        ) {
            logger.info("updating enrolment verified status")
            enrolment.verifiedAt = timeProvider.now()

            val isApproved = enrolment.reviewTasks.all { it.status == ReviewTaskStatus.APPROVED }
            enrolment.approvalStatus = if (isApproved) ApprovalStatus.APPROVED else ApprovalStatus.REJECTED

            createEnrolmentVerifiedAlert(enrolment)
                .fold({ return error(enrolment, it) }, {})

            enrolmentOutboxTaskRepository.createTasks(enrolment.enrolmentId)
                .fold({ return error(enrolment, it) }, {})
        }
        jpaOperations.persist(enrolment)
        return right(Unit)
    }

    private fun createEnrolmentVerifiedAlert(
        enrolment: PartialEnrolment
    ): Either<UpdateEnrolmentToVerifiedError, Unit> {
        logger.info("creating enrolment verified alert")
        val alert = EnrolmentVerifiedAlert.from(enrolment).fold({ return error(enrolment, it) }, { it })
        return alertCreator.create(alert).fold({ error(enrolment, it) }, { right(Unit) })
    }

    sealed interface UpdateEnrolmentToVerifiedError : CodedError {
        object UpdateEnrolmentToVerifiedFailed : DefaultCodedError(UPDATE_ENROLMENT_TO_VERIFIED_FAILED),
            UpdateEnrolmentToVerifiedError
        object EnrolmentIncomplete : DefaultCodedError(ENROLMENT_INCOMPLETE),
            UpdateEnrolmentToVerifiedError
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateEnrolmentVerifiedAt::class.java)

        private fun error(
            enrolment: PartialEnrolment,
            error: CodedError,
        ): Either.Left<UpdateEnrolmentToVerifiedError> {
            logger.error("failed to update enrolment '${enrolment.id}' to verified - $error")
            return left(UpdateEnrolmentToVerifiedFailed)
        }

        private fun error(
            enrolment: PartialEnrolment,
            error: EnrolmentErrors.EnrolmentIncomplete,
        ): Either.Left<UpdateEnrolmentToVerifiedError> {
            logger.error("failed to update enrolment '${enrolment.id}' to verified - $error")
            return left(UpdateEnrolmentToVerifiedError.EnrolmentIncomplete)
        }

        private fun error(error: UpdateEnrolmentToVerifiedError): Either.Left<UpdateEnrolmentToVerifiedError> {
            logger.error("failed to update enrolment verified for - $error")
            return left(error)
        }

        private fun failedToCreateAlert(
            enrolment: PartialEnrolment,
            error: CodedError,
        ): Either.Left<FailedToCreateAlert> {
            logger.error("failed to create EnrolmentVerifiedAlert for '${enrolment.enrolmentId}' - $error")
            return left(FailedToCreateAlert)
        }
    }
}