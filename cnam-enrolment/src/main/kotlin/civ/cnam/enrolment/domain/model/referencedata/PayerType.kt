package civ.cnam.enrolment.domain.model.referencedata

import com.fasterxml.jackson.annotation.JsonValue
import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class PayerTypeCode(@JsonValue val value: String) {
    companion object {
        val INSURED = PayerTypeCode("ASS")
    }
}

interface PayerType : OrderedCodedLabel<PayerType, PayerTypeCode>
