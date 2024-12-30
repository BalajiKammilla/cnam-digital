package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode

interface Address {
    val subPrefectureCode: SubPrefectureCode
    val agencyCode: AgencyCode
}
