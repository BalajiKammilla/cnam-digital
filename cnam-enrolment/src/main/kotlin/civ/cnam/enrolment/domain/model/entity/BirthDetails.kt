package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.value.document.BirthCertificateNumber
import java.time.LocalDate

interface BirthDetails {
    val date: LocalDate
    val certificateNumber: BirthCertificateNumber
    val certificateIssueDate: LocalDate
    val countryCode: IsoAlpha3CountryCode
    val subPrefectureCode: SubPrefectureCode?
}
