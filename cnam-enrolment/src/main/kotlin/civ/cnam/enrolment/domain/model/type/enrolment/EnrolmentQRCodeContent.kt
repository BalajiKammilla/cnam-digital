package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Address
import civ.cnam.enrolment.domain.model.type.enrolment.completed.EnrolmentDetails
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import dev.dry.core.data.model.value.MobileNumber
import java.time.LocalDate
import java.time.LocalDateTime

class EnrolmentQRCodeContent(
    val enrolmentId: EnrolmentId,
    val enrolmentCompletedAt: LocalDateTime,
    val title: Title,
    val firstNames: FirstName,
    val lastName: LastName,
    val dateOfBirth: LocalDate,
    val countryOfBirth: Country,
    val locationOfBirth: SubPrefecture?,
    val mobileNumber: MobileNumber,
    val residenceAddress: Address,
    val profession: Profession?,
    val personCategory: PersonCategory,
    val nationality: Nationality,
    //val identificationDocument: IdentificationDocument,
) {
    companion object {
        fun from(
            enrolmentId: EnrolmentId,
            completedAt: LocalDateTime,
            details: EnrolmentDetails
        ): EnrolmentQRCodeContent {
            return EnrolmentQRCodeContent(
                enrolmentId = enrolmentId,
                enrolmentCompletedAt = completedAt,
                title = details.personalDetails.title,
                firstNames = details.personalDetails.firstNames,
                lastName = details.personalDetails.lastName,
                dateOfBirth = details.birthDetails.date,
                countryOfBirth = details.birthDetails.country,
                locationOfBirth = details.birthDetails.subPrefecture,
                mobileNumber = details.contactDetails.mobileNumber,
                residenceAddress = details.address,
                profession = details.professionalDetails.profession,
                personCategory = details.professionalDetails.personCategory,
                nationality = details.personalDetails.currentNationality,
                //identificationDocument = details.identificationDocument,
            )
        }
    }
}
