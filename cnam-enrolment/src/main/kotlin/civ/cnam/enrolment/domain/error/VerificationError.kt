package civ.cnam.enrolment.domain.error

import civ.cnam.enrolment.domain.error.EnrolmentErrors.enrolmentVerifierNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.enrolmentVerifierVerificationFailed
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import dev.dry.common.error.CodedError
import dev.dry.core.outbox.OutboxErrors

sealed class VerificationError(
    error: CodedError
) : CodedError.DefaultCodedError(error), OutboxErrors.OutboxProcessingError {
    class VerifierNotFound(type: VerificationType) : VerificationError(enrolmentVerifierNotFound(type))
    class VerifierVerificationFailed(ex: Exception) : VerificationError(enrolmentVerifierVerificationFailed(ex))
}