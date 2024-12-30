package civ.cnam.enrolment.domain.model.type.enrolment.alert

import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentIncomplete
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

class EnrolmentVerifiedAlert private constructor(
    enrolmentId: EnrolmentId,
    mobileNumber: MobileNumber,
): NewTemplateAlertForSpecificUsers(
    recipients = constructRecipients(mobileNumber),
    templateName = TEMPLATE_NAME,
    parameters = constructParameters(enrolmentId),
    reference = enrolmentId.value,
    secondaryReference = null,
    reason = ENROLMENT_VERIFIED_NOTIFICATION,
) {
    companion object {
        private const val TEMPLATE_NAME = "ENROLMENT_COMPLETED"
        private val ENROLMENT_ID_PARAMETER = AlertParameterName("ENROLMENT_ID")
        private val LANGUAGE = "FR"
        private val ENROLMENT_VERIFIED_NOTIFICATION = AlertReason("ENROLMENT_VERIFIED_NOTIFICATION")

        fun from(enrolment: PartialEnrolment): Either<EnrolmentIncomplete, EnrolmentVerifiedAlert> {
            val mobileNumber = enrolment.enrolmentDetails?.contactDetails?.mobileNumber
                ?: return left(EnrolmentIncomplete)
            return right(EnrolmentVerifiedAlert(enrolment.enrolmentId, mobileNumber))
        }

        private fun constructRecipients(mobileNumber: MobileNumber): Set<TemplateAlertRecipientData> {
            return setOf(TemplateAlertRecipientData.from(mobileNumber.value, AlertChannel.SMS, LANGUAGE))
        }

        private fun constructParameters(enrolmentId: EnrolmentId): Set<AlertParameterData> {
            return setOf(AlertParameterData(name = ENROLMENT_ID_PARAMETER, value = enrolmentId.value))
        }
    }
}
