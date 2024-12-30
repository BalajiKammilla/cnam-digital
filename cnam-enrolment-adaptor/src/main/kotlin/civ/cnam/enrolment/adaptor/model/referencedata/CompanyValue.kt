package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import com.fasterxml.jackson.annotation.JsonCreator

class CompanyValue(
    override val code: CompanyCode,
    override val label: String
) : Company {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(code: String, label: String): CompanyValue =
            CompanyValue(CompanyCode(code), label)

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): CompanyValue = construct(code = values[0], label = values[1])
    }
}
