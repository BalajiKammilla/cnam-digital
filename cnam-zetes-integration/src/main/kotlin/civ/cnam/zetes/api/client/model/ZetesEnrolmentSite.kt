package civ.cnam.zetes.api.client.model

import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Address
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment

class ZetesEnrolmentSite(
    val subPrefectureCode: SubPrefectureCode,
    val agencyLabel: String,
    val agencyCode: AgencyCode
) {
    constructor(enrolment: CompletedEnrolment): this(enrolment.enrolmentDetails.address)

    constructor(address: Address): this(
        subPrefectureCode = address.subPrefecture.code,
        agencyLabel = address.agency.label,
        agencyCode = address.agency.code
    )

    override fun toString(): String = "$subPrefectureCode - $agencyLabel - $agencyCode"
}