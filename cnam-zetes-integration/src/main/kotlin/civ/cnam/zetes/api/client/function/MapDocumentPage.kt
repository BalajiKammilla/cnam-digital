package civ.cnam.zetes.api.client.function

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.type.attachment.DocumentPage
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Address
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.zetes.api.client.model.ZetesEnrolmentSite
import civ.cnam.zetes.api.client.model.ZetesSupportingDocumentPage
import org.slf4j.LoggerFactory

class MapDocumentPage(private val applyEncryption: ApplyEncryption) {
    private val logger = LoggerFactory.getLogger(MapDocumentPage::class.java)

    operator fun invoke(
        enrolment: CompletedEnrolment,
        documentAttachmentId: DocumentAttachmentId,
        documentTypeCode: DocumentTypeCode,
        page: DocumentPage,
        pageSequenceNumber: Int
    ): ZetesSupportingDocumentPage {
        logger.info("mapping page of document '$documentAttachmentId' for enrolment '${enrolment.enrolmentId}'")
        val enrolmentSite = ZetesEnrolmentSite(enrolment)
        val documentId = documentId(enrolment.enrolmentId, documentTypeCode, pageSequenceNumber)
        val encryptedBody = applyEncryption(page.image)
        logger.info("constructing document '$documentId'")
        return ZetesSupportingDocumentPage.newInstance()
            .requestUuid(enrolment.enrolmentId)
            .documentId(documentId)
            .documentType(documentTypeCode)
            .enrollmentSite(enrolmentSite)
            .documentBody(encryptedBody)
    }

    companion object {
        fun documentId(
            enrolmentId: EnrolmentId,
            documentTypeCode: DocumentTypeCode,
            documentNumber: Int
        ): String {
            return "${enrolmentId.value}-${documentTypeCode.value}-$documentNumber"
        }

        fun enrollmentSite(address: Address): String {
            val subPrefectureCode = address.subPrefecture.code.value
            val agencyLabel = address.agency.label
            val agencyCode = address.agency.code.value
            return "$subPrefectureCode - $agencyLabel - $agencyCode"
        }
    }
}
