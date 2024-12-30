package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode

interface ProfessionalDetails {
    val personTypeCode: PersonCategoryCode
    val professionCode: ProfessionCode?
    val employerCompanyCode: CompanyCode?
    val registrationNumberTypeCode: RegistrationNumberTypeCode?
    val registrationNumber: String?
}
