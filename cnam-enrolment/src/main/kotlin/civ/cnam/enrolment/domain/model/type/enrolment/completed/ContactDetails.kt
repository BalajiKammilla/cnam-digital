package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import civ.cnam.enrolment.domain.model.value.contact.LandlineNumber
import civ.cnam.enrolment.domain.model.value.contact.PostOfficeBox
import dev.dry.core.data.model.value.MobileNumber

class ContactDetails(
    val mobileNumber: MobileNumber,
    val landlineNumber: LandlineNumber,
    val email: EmailAddress?,
    val postOfficeBox: PostOfficeBox?,
)
