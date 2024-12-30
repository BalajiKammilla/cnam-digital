package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.UpdateEnrolmentCorrectiveActionPendingCount.UpdateEnrolmentCorrectiveActionPendingCountError.UpdateEnrolmentCorrectiveActionPendingCountFailed
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.UpdateEnrolmentVerifiedAt.UpdateEnrolmentToVerifiedError
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.UPDATE_ENROLMENT_CORRECTIVE_ACTION_PENDING_COUNT_FAILED
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.error.CodedError.DefaultCodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.persistence.OptimisticLockException
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateEnrolmentCorrectiveActionPendingCount(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val updateEnrolmentVerifiedAt: UpdateEnrolmentVerifiedAt,
) {
    @Throws(OptimisticLockException::class)
    operator fun invoke(
        enrolment: PartialEnrolmentEntity,
        delta: Int
    ): Either<UpdateEnrolmentCorrectiveActionPendingCountError, Unit> {
        logger.info("updating corrective action pending count for enrolment '${enrolment.id}' -- delta=$delta")
        enrolment.correctiveActionPendingCount += delta

        val correctiveActionPendingCount = enrolment.correctiveActionPendingCount
        logger.info("corrective action pending count is '$correctiveActionPendingCount'")
        if (correctiveActionPendingCount == 0) {
            updateEnrolmentVerifiedAt(enrolment).fold({ return error(it, enrolment) }, {})
        }
        jpaOperations.persist(enrolment)
        return right(Unit)
    }

    sealed interface UpdateEnrolmentCorrectiveActionPendingCountError : CodedError {
        object UpdateEnrolmentCorrectiveActionPendingCountFailed : DefaultCodedError(
            UPDATE_ENROLMENT_CORRECTIVE_ACTION_PENDING_COUNT_FAILED
        ), UpdateEnrolmentCorrectiveActionPendingCountError
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateEnrolmentCorrectiveActionPendingCount::class.java)

        fun error(
            error: UpdateEnrolmentToVerifiedError,
            enrolment: PartialEnrolment,
        ): Either.Left<UpdateEnrolmentCorrectiveActionPendingCountError> {
            logger.info("failed to update corrective action count for enrolment '${enrolment.id}' -- $error")
            return left(UpdateEnrolmentCorrectiveActionPendingCountFailed)
        }
    }
}
