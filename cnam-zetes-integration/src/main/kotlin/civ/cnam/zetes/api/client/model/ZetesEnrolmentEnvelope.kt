package civ.cnam.zetes.api.client.model

import dev.dry.common.io.encoding.Base64
import dev.dry.common.io.resource.ClassPathResource
import org.w3c.dom.Document

class ZetesEnrolmentEnvelope(document: Document) : ZetesXmlRootObject<ZetesEnrolmentEnvelope>(document) {
    companion object {
        private val XML_TEMPLATE_RESOURCE = ClassPathResource("/template/enrolment-envelope.xml")

        fun newInstance(): ZetesEnrolmentEnvelope = newInstance(XML_TEMPLATE_RESOURCE, ::ZetesEnrolmentEnvelope)

        const val REQUEST_IDENTIFIER = "requestIdentifier"
        const val ENROLLMENT_SITE = "enrollmentSite"
        const val ENROLLMENT_SERVICE_VERSION = "enrollmentServiceVersion"
        const val ENCRYPTED_REQUEST = "encryptedRequest"
    }

    val requestIdentifier: String get() = child(REQUEST_IDENTIFIER).textContent
    fun requestIdentifier(requestIdentifier: String): ZetesEnrolmentEnvelope =
        text(REQUEST_IDENTIFIER, requestIdentifier)
    fun enrollmentSite(enrollmentSite: String): ZetesEnrolmentEnvelope =
        text(ENROLLMENT_SITE, enrollmentSite.toString())
    fun enrollmentServiceVersion(enrollmentServiceVersion: String): ZetesEnrolmentEnvelope =
        text(ENROLLMENT_SERVICE_VERSION, enrollmentServiceVersion)
    fun encryptedRequest(encryptedRequest: ByteArray): ZetesEnrolmentEnvelope =
        text(ENCRYPTED_REQUEST, Base64.encodeToString(encryptedRequest))
}
