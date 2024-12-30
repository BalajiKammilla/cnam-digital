package civ.cnam.enrolment.domain.model.type.attachment

import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import com.fasterxml.jackson.annotation.JsonCreator

class DocumentAttachment(
    val purpose: SupportingDocument.Purpose,
    val documentTypeCode: DocumentTypeCode,
    val pages: List<DocumentPage>,
) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(purpose: SupportingDocument.Purpose, documentTypeCode: String, pages: List<DocumentPage>) =
            DocumentAttachment(
                purpose,
                DocumentTypeCode(documentTypeCode),
                pages
            )
    }
}
