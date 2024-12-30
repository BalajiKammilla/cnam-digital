package civ.cnam.enrolment.domain.service.verifier

import civ.cnam.enrolment.domain.function.VerifyPayerNumber
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.SubscriptionDetails
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import jakarta.enterprise.inject.Instance
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
class SubscriptionVerifierPayer(
    private val createCorrectiveAction: CreateCorrectiveAction,
    verifyPayerNumberInstance: Instance<VerifyPayerNumber>,
) : EnrolmentVerifier {
    override val type: VerificationType = VerificationType.SUBSCRIPTION_PAYER
    private val verifyPayerNumber: VerifyPayerNumber = verifyPayerNumberInstance.get()

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    override fun verify(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        val isVerified = verifySubscriptionPayer(enrolment.enrolmentDetails.subscriptionDetails)
            .fold({ return left(it) }, { it })
        return if (!isVerified) {
            createCorrectiveAction(enrolment.enrolmentId, CorrectiveActionType.SUBSCRIPTION_PAYER)
        } else {
            right(Unit)
        }
    }

    private fun verifySubscriptionPayer(subscription: SubscriptionDetails): Either<CodedError, Boolean> {
        val paidByCode = subscription.paidBy.code
        logger.info("verifying subscription payer paid by '$paidByCode'")
        if (paidByCode == PayerTypeCode.INSURED) {
            return right(true)
        }

        val cnamNumber = subscription.payer?.cnamNumber
        if (cnamNumber == null) {
            return right(false)
        }

        val verified = verifyPayerNumber(cnamNumber)
        return right(verified)
    }

    companion object {
        val logger = LoggerFactory.getLogger(SubscriptionVerifierPayer::class.java)
    }
}