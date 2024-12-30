package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.EnrolmentDetails
import jakarta.persistence.Embedded
import jakarta.validation.Valid

class EnrolmentDetailsData(
    @Embedded
    override val personalDetails: PersonalDetailsData,
    @Embedded
    override val birthDetails: BirthDetailsData,
    @Embedded
    override val address: AddressData,
    @get:Valid
    @Embedded
    override val contactDetails: ContactDetailsData,
    @Embedded
    override val professionalDetails: ProfessionalDetailsData,
    @Embedded
    override val subscriptionDetails: SubscriptionDetailsData
) : EnrolmentDetails
