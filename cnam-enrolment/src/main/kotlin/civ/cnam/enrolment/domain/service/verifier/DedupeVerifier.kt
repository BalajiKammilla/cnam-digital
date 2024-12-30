package civ.cnam.enrolment.domain.service.verifier

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateOrUpdateDedupeMatch
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
class DedupeVerifier(
    private val createOrUpdateDedupeMatch: CreateOrUpdateDedupeMatch,
) : EnrolmentVerifier {
    override val type: VerificationType = VerificationType.DEDUPE

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    override fun verify(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        return createOrUpdateDedupeMatch(enrolment).map {}
    }
}
