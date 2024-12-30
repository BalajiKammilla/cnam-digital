package civ.cnam.enrolment.domain.model.value.document

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class DocumentNumber @JsonCreator constructor(@JsonValue val value: String) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun construct(value: String): DocumentNumber = DocumentNumber(value)
    }
}
