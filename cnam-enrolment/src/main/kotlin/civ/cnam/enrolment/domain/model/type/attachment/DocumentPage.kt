package civ.cnam.enrolment.domain.model.type.attachment

import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.constraints.NotEmpty

/*@JvmInline
value*/ class DocumentPage @JsonCreator constructor(@get:NotEmpty val image: ByteArray) {
    companion object {
        //@JsonCreator
        @JvmStatic
        fun from(image: ByteArray): DocumentPage = DocumentPage(image)
    }
}
