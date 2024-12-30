package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.MaritalStatus
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName

class PersonalDetails(
    val title: Title,
    val firstNames: FirstName,
    val lastName: LastName,
    val currentNationality: Nationality,
    val maritalStatus: MaritalStatus,
    val maidenName: String?,
)
