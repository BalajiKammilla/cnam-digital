package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.PayerType
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import com.fasterxml.jackson.annotation.JsonCreator

class PayerTypeValue(
    override val code: PayerTypeCode,
    override val label: String,
    override val ordinal: Int,
) : PayerType {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(ordinal: String, code: String, label: String): PayerTypeValue =
            PayerTypeValue(PayerTypeCode(code), label, ordinal.toInt())

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): PayerTypeValue = construct(
            ordinal = values[0],
            code = values[1],
            label = values[2]
        )
    }
}
