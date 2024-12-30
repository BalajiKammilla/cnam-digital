package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.CodedLabel

@JvmInline
value class SubPrefectureCode(val value: String) {
    override fun toString(): String = value
}

interface SubPrefecture : CodedLabel<SubPrefecture, SubPrefectureCode>