package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberType

class ProfessionalDetails(
    val personCategory: PersonCategory,
    val employer: Company?,
    val registrationNumberType: RegistrationNumberType?,
    val registrationNumber: String?,
    val profession: Profession?
)