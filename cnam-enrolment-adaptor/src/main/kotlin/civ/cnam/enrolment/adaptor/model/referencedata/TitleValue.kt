package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import com.fasterxml.jackson.annotation.JsonCreator

class TitleValue(
    override val code: TitleCode,
    override val label: String,
) : Title {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String): TitleValue =
            TitleValue(TitleCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): TitleValue = construct(code = values[0], label = values[1])
    }
}
