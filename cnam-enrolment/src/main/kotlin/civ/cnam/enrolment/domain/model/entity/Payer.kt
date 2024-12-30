package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Payer
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import java.time.LocalDate

interface NaturalPersonPayer : Payer {
    val enrolmentId: EnrolmentId
    val firstName: FirstName
    val lastName: LastName
}

interface LegalPersonPayer : Payer {
    val businessEntityType: String
    val cc: String
    val companyCode: CompanyCode
    val creationDate: LocalDate
}
