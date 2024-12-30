package civ.cnam.enrolment.domain.model.type.enrolment.alert

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction.EnrolmentDetailsNotFound
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.alert.domain.model.type.AlertParameterData
import dev.dry.alert.domain.model.type.TemplateAlertRecipientData
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.alert.domain.model.value.AlertParameterName
import dev.dry.alert.domain.model.value.AlertReason
import dev.dry.alert.domain.service.AlertCreator.NewTemplateAlertForSpecificUsers
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.data.model.value.MobileNumber

class CorrectiveActionRequiredAlert(
    smsRecipient: MobileNumber,
    userRecipient: MobileNumber,
    enrolmentId: EnrolmentId,
    reason: CorrectiveActionReason,
): NewTemplateAlertForSpecificUsers(
    recipients = constructRecipients(smsRecipient = smsRecipient, userRecipient = userRecipient),
    templateName = TEMPLATE_NAME,
    parameters = constructParameters(reason),
    reference = enrolmentId.value,
    secondaryReference = null,
    reason = constructAlertReason(reason),
) {
    enum class CorrectiveActionReason(val text: String) {
        SUBSCRIPTION_PAYER_NOT_VERIFIED("Le payeur de l'abonnement n'a pas été vérifié"),
        IDENTITY_DOCUMENT_REJECTED("Pièce d'identité rejetée"),
    }

    companion object {
        private const val TEMPLATE_NAME = "CORRECTIVE_ACTION_REQUIRED"
        private val REASON_PARAMETER = AlertParameterName("REASON")
        private val LANGUAGE = "FR"
        private val UPDATE_SUBSCRIPTION_PAYER = AlertReason("UPDATE_SUBSCRIPTION_PAYER")
        private val UPDATE_IDENTITY_DOCUMENT = AlertReason("UPDATE_IDENTITY_DOCUMENT")

        fun from(
            enrolment: PartialEnrolment,
            reason: CorrectiveActionReason,
        ): Either<EnrolmentDetailsNotFound, CorrectiveActionRequiredAlert> {
            val smsRecipient = enrolment.enrolmentDetails?.contactDetails?.mobileNumber
                ?: return left(EnrolmentDetailsNotFound)
            return right(CorrectiveActionRequiredAlert(
                smsRecipient = smsRecipient,
                userRecipient = enrolment.mobileNumber,
                enrolment.enrolmentId,
                reason
            ))
        }

        fun from(
            enrolment: CompletedEnrolment,
            reason: CorrectiveActionReason,
        ): CorrectiveActionRequiredAlert {
            val smsRecipient = enrolment.enrolmentDetails.contactDetails.mobileNumber
            return CorrectiveActionRequiredAlert(
                smsRecipient = smsRecipient,
                userRecipient = enrolment.mobileNumber,
                enrolment.enrolmentId,
                reason
            )
        }

        private fun constructRecipients(
            smsRecipient: MobileNumber,
            userRecipient: MobileNumber
        ): Set<TemplateAlertRecipientData> {
            return setOf(
                TemplateAlertRecipientData.from(smsRecipient.value, AlertChannel.SMS, LANGUAGE),
                TemplateAlertRecipientData.from(userRecipient.value, AlertChannel.NONE, LANGUAGE),
            )
        }

        private fun constructParameters(reason: CorrectiveActionReason): Set<AlertParameterData> {
            return setOf(AlertParameterData(name = REASON_PARAMETER, value = reason.text))
        }

        private fun constructAlertReason(reason: CorrectiveActionReason): AlertReason {
            return when(reason) {
                CorrectiveActionReason.SUBSCRIPTION_PAYER_NOT_VERIFIED -> UPDATE_SUBSCRIPTION_PAYER
                CorrectiveActionReason.IDENTITY_DOCUMENT_REJECTED -> UPDATE_IDENTITY_DOCUMENT
            }
        }
    }
}
