package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import com.fasterxml.jackson.annotation.JsonCreator

class SubPrefectureValue(
    override val code: SubPrefectureCode,
    override val label: String,
) : SubPrefecture {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String): SubPrefectureValue =
            SubPrefectureValue(SubPrefectureCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): SubPrefectureValue = construct(code = values[0], label = values[1])
    }
}
