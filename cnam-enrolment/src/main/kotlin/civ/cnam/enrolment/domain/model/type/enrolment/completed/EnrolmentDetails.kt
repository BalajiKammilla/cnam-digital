package civ.cnam.enrolment.domain.model.type.enrolment.completed

class EnrolmentDetails(
    val personalDetails: PersonalDetails,
    val birthDetails: BirthDetails,
    val address: Address,
    val contactDetails: ContactDetails,
    val professionalDetails: ProfessionalDetails,
    val subscriptionDetails: SubscriptionDetails,
)
