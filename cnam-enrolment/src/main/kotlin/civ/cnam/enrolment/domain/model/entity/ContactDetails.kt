package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import civ.cnam.enrolment.domain.model.value.contact.LandlineNumber
import civ.cnam.enrolment.domain.model.value.contact.PostOfficeBox
import dev.dry.core.data.model.value.MobileNumber

interface ContactDetails {
    val mobileNumber: MobileNumber
    val landlineNumber: LandlineNumber
    val email: EmailAddress?
    val postOfficeBox: PostOfficeBox?
}
