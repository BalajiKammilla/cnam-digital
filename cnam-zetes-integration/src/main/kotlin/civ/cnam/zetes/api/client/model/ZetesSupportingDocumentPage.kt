package civ.cnam.zetes.api.client.model

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.io.encoding.Base64
import dev.dry.common.io.resource.ClassPathResource
import org.w3c.dom.Document

class ZetesSupportingDocumentPage(document: Document) : ZetesXmlRootObject<ZetesSupportingDocumentPage>(document) {
    companion object {
        private val XML_TEMPLATE_RESOURCE = ClassPathResource("/template/document.xml")

        fun newInstance(): ZetesSupportingDocumentPage = newInstance(XML_TEMPLATE_RESOURCE, ::ZetesSupportingDocumentPage)

        const val REQUEST_UUID = "requestUuid"
        const val ENROLMENT_SITE = "enrollmentSite"
        const val DOCUMENT_TYPE = "documentType"
        const val DOCUMENT_ID = "documentId"
        const val DOCUMENT_BODY = "documentBody"
    }

    fun requestUuid(requestUuid: EnrolmentId): ZetesSupportingDocumentPage =
        text(REQUEST_UUID, requestUuid.value)
    val requestUuid: String = child(REQUEST_UUID).textContent
    fun enrollmentSite(enrollmentSite: ZetesEnrolmentSite): ZetesSupportingDocumentPage =
        text(ENROLMENT_SITE, enrollmentSite.toString())
    fun documentType(documentTypeCode: DocumentTypeCode): ZetesSupportingDocumentPage =
        text(DOCUMENT_TYPE, documentTypeCode.value)
    fun documentId(documentId: String): ZetesSupportingDocumentPage =
        text(DOCUMENT_ID, documentId)
    val documentId: String = child(DOCUMENT_ID).textContent
    fun documentBody(documentBody: ByteArray): ZetesSupportingDocumentPage =
        text(DOCUMENT_BODY, Base64.encodeToString(documentBody, Charsets.UTF_8))
    val documentBody: String = child(DOCUMENT_BODY).textContent
}
