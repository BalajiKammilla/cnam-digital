package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture

class Address(
    val subPrefecture: SubPrefecture,
    val agency: Agency
)
