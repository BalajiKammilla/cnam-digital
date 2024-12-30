package civ.cnam.enrolment.domain.model.type.enrolment.alert

import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.alert.domain.model.type.AlertParameterData
import dev.dry.alert.domain.model.type.TemplateAlertRecipientData
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.alert.domain.model.value.AlertParameterName
import dev.dry.alert.domain.model.value.AlertReason
import dev.dry.alert.domain.service.AlertCreator.NewTemplateAlertForSpecificUsers

class EnrolmentCompletedAlert(enrolment: CompletedEnrolment): NewTemplateAlertForSpecificUsers(
    recipients = constructRecipients(enrolment),
    templateName = TEMPLATE_NAME,
    parameters = constructParameters(enrolment),
    reference = enrolment.enrolmentId.value,
    secondaryReference = null,
    reason = ENROLMENT_COMPLETED_NOTIFICATION,
) {
    companion object {
        private const val TEMPLATE_NAME = "ENROLMENT_COMPLETED"
        private val ENROLMENT_ID_PARAMETER = AlertParameterName("ENROLMENT_ID")
        private val LANGUAGE = "FR"
        private val ENROLMENT_COMPLETED_NOTIFICATION = AlertReason("ENROLMENT_COMPLETED_NOTIFICATION")

        fun constructRecipients(enrolment: CompletedEnrolment): Set<TemplateAlertRecipientData> {
            val mobileNumber = enrolment.enrolmentDetails.contactDetails.mobileNumber
            return setOf(TemplateAlertRecipientData.from(mobileNumber.value, AlertChannel.SMS, LANGUAGE))
        }

        fun constructParameters(enrolment: CompletedEnrolment): Set<AlertParameterData> {
            return setOf(AlertParameterData(name = ENROLMENT_ID_PARAMETER, value = enrolment.enrolmentId.value))
        }
    }
}
