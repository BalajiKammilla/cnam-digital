package civ.cnam.enrolment.domain.command

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType.SUBSCRIPTION_PAYER
import civ.cnam.enrolment.domain.model.mutation.enrolment.CompleteCorrectiveAction
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError.CorrectiveActionNotFound
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError.InvalidSubscriptionDetailsUpdate
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.repository.VerificationOutboxTaskRepository
import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.DocumentPage
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.service.EnrolmentWorkflow
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.transaction.Transaction
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateSubscriptionDetails(
    private val updateSubscription: UpdateSubscription,
    private val completeCorrectiveAction: CompleteCorrectiveAction,
    private val verificationOutboxTaskRepository: VerificationOutboxTaskRepository,
    private val enrolmentWorkflow: EnrolmentWorkflow,
    private val enrolmentRepository: EnrolmentRepository,
    private val tx: Transaction,
) {
    operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
        subscriptionDetails: SubscriptionDetailsData?,
        payerPoofOfIdentity: PayerProofOfIdentity?,
    ): Either<UpdateSubscriptionError, Unit> {
        if (subscriptionDetails == null && payerPoofOfIdentity == null) {
            left(InvalidSubscriptionDetailsUpdate)
        }

        return tx {
            updateSubscriptionDetails(enrolmentId, mobileNumber, subscriptionDetails, payerPoofOfIdentity)
        }
    }
    
    private fun updateSubscriptionDetails(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
        subscriptionDetails: SubscriptionDetailsData?,
        payerPoofOfIdentity: PayerProofOfIdentity?,
    ): Either<UpdateSubscriptionError, Unit> {
        val enrolment = enrolmentRepository.findPartialEnrolmentOrNull(enrolmentId)
            ?: return error(EnrolmentNotFound)

        if (subscriptionDetails != null) {
            updateSubscription(enrolmentId, mobileNumber, subscriptionDetails)
                .fold({ return error(it) }, {})
        }

        if (payerPoofOfIdentity != null) {
            val documentAttachment = DocumentAttachment(
                SupportingDocument.Purpose.PAYER_PROOF_OF_IDENTITY,
                payerPoofOfIdentity.documentTypeCode,
                payerPoofOfIdentity.pages,
            )
            enrolmentWorkflow.createOrReplaceSupportingDocument(enrolmentId, documentAttachment)
                .fold({ return error(EnrolmentNotFound) }, { it })
        }

        completeCorrectiveAction(enrolment.id, CorrectiveActionType.SUBSCRIPTION_PAYER)
            .fold({ return error(CorrectiveActionNotFound) }, {})

        verificationOutboxTaskRepository.createTasks(enrolmentId, setOf(SUBSCRIPTION_PAYER))
            .fold({ return error(EnrolmentNotFound) }, {})

        return right(Unit)
    }

    class PayerProofOfIdentity(
        val documentTypeCode: DocumentTypeCode,
        val pages: List<DocumentPage>,
    )

    companion object {
        val logger = LoggerFactory.getLogger(UpdateSubscriptionDetails::class.java)

        private fun error(error: UpdateSubscriptionError): Either.Left<UpdateSubscriptionError> {
            logger.info("update subscription details failed -- $error")
            return left(error)
        }
    }
}
