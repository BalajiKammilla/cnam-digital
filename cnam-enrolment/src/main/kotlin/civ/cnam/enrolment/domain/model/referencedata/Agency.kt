package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.CodedLabel


@JvmInline
value class AgencyCode(val value: String) {
    override fun toString(): String = value
}

interface Agency : CodedLabel<Agency, AgencyCode> {
    val subPrefectureCode: SubPrefectureCode
    val address: String
}
