package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import com.fasterxml.jackson.annotation.JsonCreator

class NationalityValue(
    override val code: IsoAlpha3CountryCode,
    override val label: String,
    override val ordinal: Int,
) : Nationality {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String, ordinal: String): NationalityValue =
            NationalityValue(IsoAlpha3CountryCode(code), label, ordinal.toInt())

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): NationalityValue = construct(
            code = values[0],
            label = values[1],
            ordinal = values[2]
        )
    }
}
