package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.value.personal.LastName

interface PersonalDetails {
    val titleCode: TitleCode
    val firstNames: FirstName
    val lastName: LastName
    val currentNationalityCode: IsoAlpha3CountryCode
    val maritalStatusCode: MaritalStatusCode
    val maidenName: String?
}
