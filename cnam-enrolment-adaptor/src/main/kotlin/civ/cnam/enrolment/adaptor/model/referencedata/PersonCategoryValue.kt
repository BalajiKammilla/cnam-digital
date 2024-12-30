package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import com.fasterxml.jackson.annotation.JsonCreator

class PersonCategoryValue(
    override val ordinal: Int,
    override val code: PersonCategoryCode,
    override val label: String,
) : PersonCategory {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(ordinal: String, code: String, label: String): PersonCategoryValue =
            PersonCategoryValue(ordinal.toInt(), PersonCategoryCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): PersonCategoryValue = construct(
            ordinal = values[0],
            code = values[1],
            label = values[2]
        )
    }
}
