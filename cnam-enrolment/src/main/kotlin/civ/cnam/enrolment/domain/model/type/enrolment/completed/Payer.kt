package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import java.time.LocalDate

interface Payer {
    val cnamNumber: CNAMNumber
}

class NaturalPersonPayer(
    override val cnamNumber: CNAMNumber,
    val enrolmentId: EnrolmentId,
    val firstName: FirstName,
    val lastName: LastName,
) : Payer

class LegalPersonPayer(
    override val cnamNumber: CNAMNumber,
    val businessEntityType: String,
    val cc: String,
    val company: Company,
    val creationDate: LocalDate,
) : Payer
