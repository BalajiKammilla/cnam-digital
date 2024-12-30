package civ.cnam.zetes.api.client

import civ.cnam.zetes.api.client.exception.ZetesAttachDocumentFailedException
import civ.cnam.zetes.api.client.exception.ZetesCreateEnrolmentFailedException
import civ.cnam.zetes.api.client.function.ApplyEncryption
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope
import civ.cnam.zetes.api.client.model.ZetesSupportingDocumentPage
import dev.dry.common.exception.Exceptions
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import org.slf4j.LoggerFactory
import java.net.URI

class ZetesApiClient(baseUri: URI, private val applyEncryption: ApplyEncryption) {
    private val logger = LoggerFactory.getLogger(civ.cnam.zetes.api.client.ZetesApiClient::class.java)
    companion object {
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val ENROLMENT_CONTENT_TYPE = "application/vnd.zetes.cnamenrollment.encrypted+xml"
        private const val DOCUMENT_CONTENT_TYPE = "application/vnd.zetes.documents.encrypted+xml"
        private const val ENROLMENT_URL = "https://digipaygateway.com:46180/cnamzetes/public/api/enrollment"
    }

    private val baseUri = baseUri.toString()
    private val client = JavaHttpClient()

    fun createEnrolment(envelope: ZetesEnrolmentEnvelope) {
        logger.info("sending zetes enrolment request")
        val response = try {
            val requestBody = envelope.toXmlString()
            val request = Request(POST, "$baseUri/enrollment")
                .header(
                    civ.cnam.zetes.api.client.ZetesApiClient.Companion.CONTENT_TYPE_HEADER,
                    civ.cnam.zetes.api.client.ZetesApiClient.Companion.ENROLMENT_CONTENT_TYPE
                )
                .body(requestBody)
            client(request)
        } catch(ex: Exception) {
            logger.error("sending zetes enrolment request failed -- {}", Exceptions.getMessageChain(ex))
            throw ZetesCreateEnrolmentFailedException(envelope.requestIdentifier)
        }

        logger.info("sending zetes enrolment request completed with status '${response.status}'")
        if (response.status != Status.OK) {
            throw ZetesCreateEnrolmentFailedException(envelope.requestIdentifier, response.status)
        }
    }

    fun attachDocumentPage(document: ZetesSupportingDocumentPage) {
        logger.info("attaching zetes document page")
        val response = try {
            val requestBody = document.toXmlString()
            val request = Request(POST, "$baseUri/documents")
                .header(
                    civ.cnam.zetes.api.client.ZetesApiClient.Companion.CONTENT_TYPE_HEADER,
                    civ.cnam.zetes.api.client.ZetesApiClient.Companion.DOCUMENT_CONTENT_TYPE
                )
                .body(requestBody)
            client(request)
        } catch(ex: Exception) {
            logger.error("attaching zetes document page failed -- {}", Exceptions.getMessageChain(ex))
            throw ZetesAttachDocumentFailedException(
                id = document.requestUuid,
                documentId = document.documentId,
            )
        }

        logger.info("attaching zetes document page completed with status '${response.status}'")
        if (response.status != Status.OK) {
            throw ZetesAttachDocumentFailedException(
                id = document.requestUuid,
                documentId = document.documentId,
                status = response.status,
            )
        }
    }
}
