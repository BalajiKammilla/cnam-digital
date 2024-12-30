package civ.cnam.enrolment.domain.model.referencedata

import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class DocumentTypeCode(val value: String) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun construct(value: String): DocumentTypeCode = DocumentTypeCode(value)
    }
}

enum class DocumentCategory {
    PROOF_OF_IDENTITY,
    PROOF_OF_MARRIAGE,
    PROOF_OF_PROFESSION,
    OTHER,
    ;
}

interface DocumentType : OrderedCodedLabel<DocumentType, DocumentTypeCode> {
    val category: DocumentCategory
    val numberOfPages: Int
}
