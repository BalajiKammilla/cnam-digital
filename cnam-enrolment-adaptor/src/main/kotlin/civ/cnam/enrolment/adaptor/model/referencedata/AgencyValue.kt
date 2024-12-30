package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import com.fasterxml.jackson.annotation.JsonCreator

class AgencyValue(
    override val code: AgencyCode,
    override val label: String,
    override val address: String,
    override val subPrefectureCode: SubPrefectureCode,
) : Agency {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(
            code: String,
            label: String,
            address: String,
            subPrefectureCode: String,
        ): AgencyValue = AgencyValue(
            code = AgencyCode(code),
            label = label,
            address = address,
            subPrefectureCode = SubPrefectureCode(subPrefectureCode)
        )

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): AgencyValue = construct(
            code = values[0],
            label = values[1],
            address = values[2],
            subPrefectureCode = values[3]
        )
    }
}
