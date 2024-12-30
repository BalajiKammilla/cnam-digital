package civ.cnam.enrolment.domain.command

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.SERVER_ERROR
import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType.DEDUPE
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType.IDENTITY_DOCUMENT
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType.SUBSCRIPTION_PAYER
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.repository.VerificationOutboxTaskRepository
import civ.cnam.enrolment.domain.model.type.enrolment.alert.EnrolmentCompletedAlert
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.alert.domain.service.AlertCreator
import dev.dry.common.error.CodedError
import dev.dry.common.error.toError
import dev.dry.common.function.Either
import dev.dry.common.function.flatMap
import dev.dry.common.function.map
import dev.dry.common.function.mapLeft
import dev.dry.common.time.TimeProvider
import dev.dry.core.data.model.value.MobileNumber
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CompleteEnrolment(
    private val enrolmentRepository: EnrolmentRepository,
    private val verificationOutboxTaskRepository: VerificationOutboxTaskRepository,
    private val mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
    private val timeProvider: TimeProvider,
    private val alertCreator: AlertCreator,
) {
    @Transactional
    operator fun invoke(enrolmentId: EnrolmentId, mobileNumber: MobileNumber): Either<CodedError, Unit> {
        return enrolmentRepository.findPartialEnrolment(enrolmentId, mobileNumber)
            .flatMap(::mapToCompletedEnrolment)
            .flatMap(::updateCompletedAt)
            .mapLeft { throw it.toException() } // trigger a transaction rollback - TODO("manually demarcate transaction")
            .flatMap(::onEnrolmentCompleted)
    }

    private fun onEnrolmentCompleted(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        return createEnrolmentCompletedAlert(enrolment)
            .flatMap {
                val verificationTypes =
                    setOf(DEDUPE, IDENTITY_DOCUMENT, SUBSCRIPTION_PAYER)
                verificationOutboxTaskRepository.createTasks(enrolment.enrolmentId, verificationTypes)
            }
            .flatMap {
                enrolmentRepository.updateApprovalStatus(enrolment.enrolmentId, ApprovalStatus.PENDING)
            }
            .mapLeft { throw it.toException() } // trigger a transaction rollback - TODO("manually demarcate transaction")
    }

    private fun mapToCompletedEnrolment(partial: PartialEnrolment): Either<CodedError, CompletedEnrolment> {
        val completedAt = timeProvider.now()
        return mapPartialToCompletedEnrolment(partial, completedAt)
    }

    private fun updateCompletedAt(
        completedEnrolment: CompletedEnrolment,
    ): Either<EnrolmentNotFound, CompletedEnrolment> {
        return enrolmentRepository.updateCompletedAt(
            completedEnrolment.enrolmentId,
            completedEnrolment.completedAt,
        ).map { completedEnrolment }
    }

    private fun createEnrolmentCompletedAlert(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        return try {
            alertCreator.create(EnrolmentCompletedAlert(enrolment))
                .mapLeft {
                    failedToCreateAlert(enrolment, it)
                }.map {}
        } catch (ex: Exception) {
            Either.left(failedToCreateAlert(enrolment, SERVER_ERROR.toError(ex)))
        }
    }

    private fun failedToCreateAlert(enrolment: CompletedEnrolment, error: CodedError): CodedError {
        logger.error("failed to create alert for completion of enrolment '${enrolment.enrolmentId}' - $error")
        return error
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CompleteEnrolment::class.java)
    }
}
