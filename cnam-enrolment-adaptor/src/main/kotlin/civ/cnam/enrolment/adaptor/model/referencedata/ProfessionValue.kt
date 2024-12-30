package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import com.fasterxml.jackson.annotation.JsonCreator

class ProfessionValue(
    override val ordinal: Int,
    override val code: ProfessionCode,
    override val label: String
) : Profession {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(ordinal: String, code: String, label: String): ProfessionValue =
            ProfessionValue(ordinal.toInt(), ProfessionCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): ProfessionValue = construct(
            ordinal = values[0],
            code = values[1],
            label = values[2]
        )
    }
}
