package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesIdentificationDocument(element: Element): ZetesXmlObject<ZetesIdentificationDocument>(element) {
    companion object {
        const val DOCUMENT_NUMBER = "documentNumber"
        const val DOCUMENT_TYPE = "documentType"
    }

    fun documentNumber(documentNumber: String): ZetesIdentificationDocument =
        text(DOCUMENT_NUMBER, documentNumber)
    fun documentType(documentType: String): ZetesIdentificationDocument =
        text(DOCUMENT_TYPE, documentType)
}
