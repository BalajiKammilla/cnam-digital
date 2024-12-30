package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.MaritalStatus
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import com.fasterxml.jackson.annotation.JsonCreator

class MaritalStatusValue(
    override val ordinal: Int,
    override val code: MaritalStatusCode,
    override val label: String
) : MaritalStatus {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(ordinal: String, code: String, label: String): MaritalStatusValue =
            MaritalStatusValue(ordinal.toInt(), MaritalStatusCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): MaritalStatusValue = construct(
            ordinal = values[0],
            code = values[1],
            label = values[2]
        )
    }
}
