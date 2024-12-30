package civ.cnam.api.data

import com.fasterxml.jackson.annotation.JsonInclude

data class TestSupportingDocuments(
    val purpose: String?,
    val documentTypeCodes: List<String>?,
    val documentTypeOptions: List<DocumentTypeOptions>
)

data class DocumentTypeOptions(
    val ordinal: Int?,
    val numberOfPages: Int?,
    val category: String?,
    val code: String?,
    val label: String?
)