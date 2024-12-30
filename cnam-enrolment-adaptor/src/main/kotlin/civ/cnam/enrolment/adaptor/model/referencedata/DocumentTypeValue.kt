package civ.cnam.enrolment.adaptor.model.referencedata

import civ.cnam.enrolment.domain.model.referencedata.DocumentCategory
import civ.cnam.enrolment.domain.model.referencedata.DocumentType
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import com.fasterxml.jackson.annotation.JsonCreator

class DocumentTypeValue(
    override val ordinal: Int,
    override val numberOfPages: Int,
    override val category: DocumentCategory,
    override val code: DocumentTypeCode,
    override val label: String,
) : DocumentType {
    companion object {
        @JvmStatic
        @JsonCreator
        fun construct(
            ordinal: String,
            numberOfPages: String,
            category: String,
            code: String,
            label: String
        ): DocumentTypeValue =
            DocumentTypeValue(
                ordinal = ordinal.toInt(),
                numberOfPages = numberOfPages.toInt(),
                category = DocumentCategory.valueOf(category),
                code = DocumentTypeCode(code),
                label = label,
            )

        @JvmStatic
        @JsonCreator
        fun construct(values: List<String>): DocumentTypeValue = construct(
            ordinal = values[0],
            numberOfPages = values[1],
            category = values[2],
            code = values[3],
            label = values[4]
        )
    }
}
