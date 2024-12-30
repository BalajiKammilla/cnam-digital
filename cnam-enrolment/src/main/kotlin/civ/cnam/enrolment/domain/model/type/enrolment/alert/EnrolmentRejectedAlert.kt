package civ.cnam.enrolment.domain.model.type.enrolment.alert

import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentDetailsNotFound
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
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

class EnrolmentRejectedAlert(
    recipient: MobileNumber,
    enrolmentId: EnrolmentId,
    reason: EnrolmentRejectedReason,
): NewTemplateAlertForSpecificUsers(
    recipients = constructRecipients(recipient),
    templateName = TEMPLATE_NAME,
    parameters = constructParameters(reason),
    reference = enrolmentId.value,
    secondaryReference = null,
    reason = ENROLMENT_REJECTED_NOTIFICATION,
) {
    enum class EnrolmentRejectedReason(val text: String) {
        DUPLICATE_ENROLMENT_FOUND("Inscription en double trouvée")
    }

    companion object {
        private const val TEMPLATE_NAME = "ENROLMENT_REJECTED"
        private val REASON_PARAMETER = AlertParameterName("REASON")
        private val LANGUAGE = "FR"
        private val ENROLMENT_REJECTED_NOTIFICATION = AlertReason("ENROLMENT_REJECTED_NOTIFICATION")

        fun from(
            enrolment: PartialEnrolment,
            reason: EnrolmentRejectedReason,
        ): Either<EnrolmentDetailsNotFound, EnrolmentRejectedAlert> {
            val mobileNumber = enrolment.enrolmentDetails?.contactDetails?.mobileNumber
                ?: return left(EnrolmentDetailsNotFound)
            return right(EnrolmentRejectedAlert(mobileNumber, enrolment.enrolmentId, reason))
        }

        private fun constructRecipients(mobileNumber: MobileNumber): Set<TemplateAlertRecipientData> {
            return setOf(TemplateAlertRecipientData.from(mobileNumber.value, AlertChannel.SMS, LANGUAGE))
        }

        private fun constructParameters(reason: EnrolmentRejectedReason): Set<AlertParameterData> {
            return setOf(AlertParameterData(name = REASON_PARAMETER, value = reason.text))
        }
    }
}
