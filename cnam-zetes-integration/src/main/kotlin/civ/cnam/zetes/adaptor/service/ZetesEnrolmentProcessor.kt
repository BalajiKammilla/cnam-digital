package civ.cnam.zetes.adaptor.service

import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.model.query.attachment.GetAttachment
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.repository.EnrolmentOutboxTaskRepository
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.attachment.DocumentPage
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.service.AbstractEnrolmentProcessor
import civ.cnam.zetes.api.client.ZetesApiClient
import civ.cnam.zetes.api.client.function.MapDocumentPage
import civ.cnam.zetes.api.client.function.MapEnrolment
import civ.cnam.zetes.api.client.function.PrepareEnrolmentRequestEnvelope
import civ.cnam.zetes.api.client.model.ZetesEnrolmentSite
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.time.TimeProvider
import org.slf4j.LoggerFactory

class ZetesEnrolmentProcessor(
    timeProvider: TimeProvider,
    enrolmentRepository: EnrolmentRepository,
    enrolmentOutboxTaskRepository: EnrolmentOutboxTaskRepository,
    mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
    getAttachment: GetAttachment,
    private val api: ZetesApiClient,
    private val mapEnrolment: MapEnrolment,
    private val prepareEnrolmentRequestEnvelope: PrepareEnrolmentRequestEnvelope,
    private val mapDocumentPage: MapDocumentPage,
) : AbstractEnrolmentProcessor(
    timeProvider,
    enrolmentRepository,
    enrolmentOutboxTaskRepository,
    mapPartialToCompletedEnrolment,
    getAttachment
) {
    private val logger = LoggerFactory.getLogger(ZetesEnrolmentProcessor::class.java)

    override fun processEnrolment(
        enrolment: CompletedEnrolment,
        photoAttachment: PhotoAttachment,
        fingerprintsAttachment: FingerprintsAttachment?,
        signatureAttachment: SignatureAttachment?
    ): Either<CodedError, Unit> {
        logger.info("submitting enrolment '${enrolment.enrolmentId}'")
        val enrolmentSite = ZetesEnrolmentSite(enrolment)
        val zetesEnrolement = mapEnrolment(
            enrolment,
            enrolmentSite,
            photoAttachment,
            fingerprintsAttachment?.fingerprints,
            signatureAttachment
        )
        val zetesEnrolmentEnvelope = prepareEnrolmentRequestEnvelope(zetesEnrolement, enrolmentSite)
        api.createEnrolment(zetesEnrolmentEnvelope)
        logger.info("submitting enrolment '${enrolment.enrolmentId}' completed")
        return Either.right(Unit)
    }

    override fun processDocumentPage(
        enrolment: CompletedEnrolment,
        documentAttachmentId: DocumentAttachmentId,
        documentTypeCode: DocumentTypeCode,
        page: DocumentPage,
        pageSequenceNumber: Int
    ): Either<CodedError, Unit> {
        logger.info("attaching document with id '$documentAttachmentId' for enrolment '${enrolment.enrolmentId}'")
        val zetesPage = mapDocumentPage(
            enrolment,
            documentAttachmentId,
            documentTypeCode,
            page,
            pageSequenceNumber
        )
        api.attachDocumentPage(zetesPage)
        logger.info("attaching document completed")
        return Either.right(Unit)
    }
}
