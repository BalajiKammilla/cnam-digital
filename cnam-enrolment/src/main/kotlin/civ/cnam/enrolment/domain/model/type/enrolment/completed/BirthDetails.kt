package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.value.document.BirthCertificateNumber
import java.time.LocalDate

class BirthDetails(
    val date: LocalDate,
    val certificateNumber: BirthCertificateNumber,
    val certificateIssueDate: LocalDate,
    val country: Country,
    val subPrefecture: SubPrefecture?,
)