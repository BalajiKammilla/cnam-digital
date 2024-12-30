package civ.cnam.enrolment.domain.model.referencedata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class MaritalStatusCode(val value: String) {
    companion object {
        val MARRIED = MaritalStatusCode("MAR")
    }
}

@JsonIgnoreProperties("isMarried")
interface MaritalStatus : OrderedCodedLabel<MaritalStatus, MaritalStatusCode>
