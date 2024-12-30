package civ.cnam.enrolment.domain.service.verifier

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

interface EnrolmentVerifier {
    val type: VerificationType

    fun verify(enrolment: CompletedEnrolment): Either<CodedError, Unit>
}
