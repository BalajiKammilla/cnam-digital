package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberType
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import com.fasterxml.jackson.annotation.JsonCreator

class RegistrationNumberTypeValue(
    override val code: RegistrationNumberTypeCode,
    override val label: String
) : RegistrationNumberType {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String): RegistrationNumberTypeValue =
            RegistrationNumberTypeValue(RegistrationNumberTypeCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): RegistrationNumberTypeValue = construct(code = values[0], label = values[1])
    }
}
