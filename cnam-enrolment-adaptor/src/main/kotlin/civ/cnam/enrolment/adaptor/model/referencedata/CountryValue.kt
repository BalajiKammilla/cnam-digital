package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import com.fasterxml.jackson.annotation.JsonCreator

class CountryValue(
    override val code: IsoAlpha3CountryCode,
    override val label: String,
    override val ordinal: Int,
) : Country {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String, ordinal: String): CountryValue =
            CountryValue(IsoAlpha3CountryCode(code), label, ordinal.toInt())

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): CountryValue = construct(
            code = values[0],
            label = values[1],
            ordinal = values[2]
        )
    }
}
