package civ.cnam.enrolment.domain.service.verifier

import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType.IDENTITY_DOCUMENT
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
class IdentityDocumentVerifier(private val createReviewTask: CreateReviewTask) : EnrolmentVerifier {
    override val type: VerificationType = VerificationType.IDENTITY_DOCUMENT

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    override fun verify(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        return if (!enrolment.identityDocument.ocrSucceeded) {
            createReviewTask(enrolment.enrolmentId, IDENTITY_DOCUMENT)
        } else {
            Either.right(Unit)
        }
    }
}