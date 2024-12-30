package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode

interface EnrolmentDetails {
    val personalDetails: PersonalDetails
    val birthDetails: BirthDetails
    val address: Address
    val contactDetails: ContactDetails
    val professionalDetails: ProfessionalDetails
    val subscriptionDetails: SubscriptionDetails

    val requiredSupportingDocuments: List<SupportingDocument.Purpose> get() {
        val requiredSupportingDocuments = mutableListOf<SupportingDocument.Purpose>()

        if (personalDetails.maritalStatusCode == MaritalStatusCode.MARRIED) {
            requiredSupportingDocuments.add(SupportingDocument.Purpose.APPLICANT_PROOF_OF_MARRIAGE)
        }

        if (subscriptionDetails.paidByCode != PayerTypeCode.INSURED) {
            requiredSupportingDocuments.add(SupportingDocument.Purpose.PAYER_PROOF_OF_IDENTITY)
        }

        if (PERSON_CATEGORIES_REQUIRING_PROOF_OF_PROFESSION.contains(professionalDetails.personTypeCode)) {
            requiredSupportingDocuments.add(SupportingDocument.Purpose.APPLICANT_PROOF_OF_PROFESSION)
        }

        return requiredSupportingDocuments
    }

    companion object {
        private val PERSON_CATEGORIES_REQUIRING_PROOF_OF_PROFESSION = setOf(
            PersonCategoryCode("FCI"),
            PersonCategoryCode("MIL"),
            PersonCategoryCode("SAL"),
            PersonCategoryCode("REP"),
            PersonCategoryCode("RET"),
        )
    }
}
