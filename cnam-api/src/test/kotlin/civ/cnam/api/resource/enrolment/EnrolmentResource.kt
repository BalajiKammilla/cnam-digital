package civ.cnam.api.resource.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.DocumentPage
import civ.cnam.enrolment.domain.model.type.attachment.Fingerprint
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import dev.dry.common.io.encoding.Base64
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class EnrolmentResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {

    private val enrolment = EnrolmentContext

    private val contentBytes = Base64.decodeFromString("MTIzNDU2Nzg5")

    constructor(accessToken: String): this("/api/enrolment", accessToken)

    fun attachFingerprints(): Response {
        val wsqBytes = EnrolmentResource::class.java.getResource("/finger.wsq").readBytes()

        val request = FingerprintsAttachment(fingerprints = listOf(
            Fingerprint(fingerIndex = 1, image = wsqBytes),
            Fingerprint(fingerIndex = 2, image = wsqBytes),
            Fingerprint(fingerIndex = 3, image = wsqBytes),
            Fingerprint(fingerIndex = 4, image = wsqBytes),
            Fingerprint(fingerIndex = 5, image = wsqBytes),
            Fingerprint(fingerIndex = 6, image = wsqBytes),
            Fingerprint(fingerIndex = 7, image = wsqBytes),
            Fingerprint(fingerIndex = 8, image = wsqBytes),
            Fingerprint(fingerIndex = 9, image = wsqBytes),
            Fingerprint(fingerIndex = 10, image = contentBytes),
        ))
        return post("${enrolment.enrolmentId}/fingerprints", request)
    }

    fun attachIdentityDocument(
        documentTypeCode: String,
        documentNumber: String,
    ): Response {
        val request = IdentityDocumentAttachment(
            documentNumber = DocumentNumber(documentNumber),
            documentTypeCode = DocumentTypeCode(documentTypeCode),
            pages = listOf(DocumentPage(contentBytes)),
            ocrSucceeded = false,
            extractedPortraitPhoto = PhotoAttachment(contentBytes),
        )
        return post("identity-document", request)
    }

    fun attachPhoto(): Response {
        val request = PhotoAttachment(image = contentBytes)
        return post("${enrolment.enrolmentId}/photo", request)
    }

    fun attachSignature(): Response {
        val request = SignatureAttachment(image = contentBytes)
        return post("${enrolment.enrolmentId}/signature", request)
    }

    fun attachSupportingDocument(
        documentTypeCode: String,
        purpose: SupportingDocument.Purpose,
    ): Response {
        val request = DocumentAttachment(
            purpose = purpose,
            documentTypeCode = DocumentTypeCode(documentTypeCode),
            pages = listOf(DocumentPage(contentBytes)),
        )
        return post("${enrolment.enrolmentId}/document", request)
    }

    fun completeEnrolment(): Response {
        return post("${enrolment.enrolmentId}/completed", null)
    }

    fun submitEnrolmentDetails(
        request: EnrolmentDetailsData,
    ): Response {
        return put("${enrolment.enrolmentId}/details", request)
    }

    private fun <T> post(uriPath: String, request: T?): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)

        if (request != null) {
            spec.body(request)
        }

        return spec
            .post("$resourceUri/$uriPath")
            .andReturn()
    }

    private fun <T> put(uriPath: String, request: T?): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)

        if (request != null) {
            spec.body(request)
        }

        return spec
            .put("$resourceUri/$uriPath")
            .andReturn()
    }
}
