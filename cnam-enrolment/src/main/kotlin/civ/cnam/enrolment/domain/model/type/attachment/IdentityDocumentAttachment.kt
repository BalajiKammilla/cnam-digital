package civ.cnam.enrolment.domain.model.type.attachment

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import com.fasterxml.jackson.annotation.JsonCreator
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

class IdentityDocumentAttachment(
    @get:NotBlank
    val documentNumber: DocumentNumber,
    val documentTypeCode: DocumentTypeCode,
    @get:Valid
    @get:NotEmpty
    val pages: List<DocumentPage>,
    val ocrSucceeded: Boolean,
    val extractedPortraitPhoto: PhotoAttachment? = null,
) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun valueOf(
            documentNumber: String,
            documentTypeCode: String,
            pages: List<DocumentPage>,
            ocrSucceeded: Boolean,
            extractedPortraitPhoto: PhotoAttachment? = null,
        ): IdentityDocumentAttachment = IdentityDocumentAttachment(
            documentNumber = DocumentNumber(documentNumber),
            documentTypeCode = DocumentTypeCode(documentTypeCode),
            pages = pages,
            ocrSucceeded = ocrSucceeded,
            extractedPortraitPhoto = extractedPortraitPhoto,
        )
    }
}
