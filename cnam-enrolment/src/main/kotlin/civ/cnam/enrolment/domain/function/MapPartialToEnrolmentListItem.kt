package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class MapPartialToEnrolmentListItem {
    operator fun invoke(partial: PartialEnrolment): EnrolmentListItem {
        val enrolmentDetails = partial.enrolmentDetails
        return EnrolmentListItem(
            enrolmentId = partial.enrolmentId.value,
            startedAt = partial.startedAt,
            completedAt = partial.completedAt,
            verifiedAt = partial.verifiedAt,
            approvalStatus = partial.approvalStatus,
            processedAt = partial.processedAt,
            firstName = enrolmentDetails?.personalDetails?.firstNames?.value,
            lastName = enrolmentDetails?.personalDetails?.lastName?.value,
            dateOfBirth = enrolmentDetails?.birthDetails?.date,
            nationality = enrolmentDetails?.personalDetails?.currentNationalityCode,
            personCategory = enrolmentDetails?.professionalDetails?.personTypeCode,
        )
    }
}
