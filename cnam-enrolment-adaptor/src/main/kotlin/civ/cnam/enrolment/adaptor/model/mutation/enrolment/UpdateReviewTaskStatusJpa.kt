package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.query.GetEnrolmentUserJpa
import civ.cnam.enrolment.adaptor.model.query.GetReviewTaskJpa
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentUserNotFound
import civ.cnam.enrolment.domain.error.UpdateReviewTaskError
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateReviewTaskStatus
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.time.TimeProvider
import dev.dry.core.data.model.value.UserName
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateReviewTaskStatusJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val getReviewTask: GetReviewTaskJpa,
    private val getEnrolmentUser: GetEnrolmentUserJpa,
    private val updateEnrolmentReviewTaskPendingCount: UpdateEnrolmentReviewTaskPendingCount,
) : UpdateReviewTaskStatus {
    override fun invoke(
        id: ReviewTask.ID,
        updatedBy: UserName,
        status: ReviewTaskStatus,
        note: String?,
    ): Either<UpdateReviewTaskError, Unit> {
        logger.info("updating review task '$id' with status '${status}' by '$updatedBy'")

        val reviewTask = getReviewTask(id)
            .fold({ return failed(it) }, { it })

        val user = getEnrolmentUser(updatedBy) { EnrolmentUserNotFound }
            .fold({ return failed(it) }, { it })

        reviewTask.status = status
        reviewTask.note = note
        reviewTask.completedAt = timeProvider.now()
        reviewTask.completedBy = user
        jpaOperations.persist(reviewTask)

        val enrolment = reviewTask.enrolment

        updateEnrolmentReviewTaskPendingCount(enrolment, -1)

        logger.info("updating review task completed")

        return right(Unit)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateReviewTaskStatusJpa::class.java)

        private fun failed(error: UpdateReviewTaskError): Either.Left<UpdateReviewTaskError> {
            logger.info("updating review task failed - $error")
            return left(error)
        }
    }
}
