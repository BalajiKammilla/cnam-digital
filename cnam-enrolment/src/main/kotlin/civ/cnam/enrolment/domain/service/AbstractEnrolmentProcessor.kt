package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask
import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
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
import civ.cnam.enrolment.domain.model.value.attachment.IdentityDocumentAttachmentId
import dev.dry.common.error.CodedError
import dev.dry.common.error.toError
import dev.dry.common.function.Either
import dev.dry.common.function.flatMap
import dev.dry.common.function.map
import dev.dry.common.function.mapLeft
import dev.dry.common.function.onFailure
import dev.dry.common.function.onSuccess
import dev.dry.common.time.TimeProvider
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime

abstract class AbstractEnrolmentProcessor(
    private val timeProvider: TimeProvider,
    private val enrolmentRepository: EnrolmentRepository,
    private val enrolmentOutboxTaskRepository: EnrolmentOutboxTaskRepository,
    protected val mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
    private val getAttachment: GetAttachment
) : EnrolmentProcessor {
    private val logger = LoggerFactory.getLogger(AbstractEnrolmentProcessor::class.java)

    override fun process(task: EnrolmentOutboxTask, processingId: String) {
        val startedAt = timeProvider.now()
        try {
            mapPartialToCompletedEnrolment(task.enrolment)
                .map { process(task, it) }
                .onFailure { updateEnrolmentStatus(startedAt, task, processingId, it) }
                .onSuccess { updateEnrolmentStatus(startedAt, task, processingId) }
        } catch (th: Throwable) {
            updateEnrolmentStatus(startedAt, task, processingId, EnrolmentErrors.SERVER_ERROR.toError(th))
        }
    }

    private fun process(task: EnrolmentOutboxTask, enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        val enrolmentId = enrolment.enrolmentId
        val identityDocument = task.identityDocument
        val supportingDocument = task.supportingDocument
        logger.info("processing enrolment '$enrolmentId'")
        return if (identityDocument != null) {
            val attachmentId = IdentityDocumentAttachmentId(identityDocument.documentAttachmentId.value)
            val attachment = getAttachment(enrolmentId, attachmentId)
            getAttachmentPage(attachment.pages, task.pageNumber).flatMap { page ->
                processIdentityDocumentPage(enrolment, identityDocument, page, task.sequenceNumber)
            }
        } else if (supportingDocument != null) {
            val attachment = getAttachment(enrolmentId, supportingDocument.documentAttachmentId)
            getAttachmentPage(attachment.pages, task.pageNumber).flatMap { page ->
                processSupportingDocumentPage(enrolment, supportingDocument, page, task.sequenceNumber)
            }
        } else {
            processEnrolment(enrolment)
        }
    }

    private fun processEnrolment(enrolment: CompletedEnrolment): Either<CodedError, Unit> {
        val enrolmentId = enrolment.enrolmentId

        val photoAttachment = getAttachment(enrolmentId, enrolment.photoAttachmentId)

        val fingerprintsAttachmentId = enrolment.fingerprintsAttachmentId
        val fingerprintsAttachment = if (fingerprintsAttachmentId != null) {
            getAttachment(enrolmentId, fingerprintsAttachmentId)
        } else null

        val signatureAttachmentId = enrolment.signatureAttachmentId
        val signatureAttachment = if (signatureAttachmentId != null) {
            getAttachment(enrolmentId, signatureAttachmentId)
        } else null

        return processEnrolment(
            enrolment,
            photoAttachment,
            fingerprintsAttachment,
            signatureAttachment,
        )
    }

    protected abstract fun processEnrolment(
        enrolment: CompletedEnrolment,
        photoAttachment: PhotoAttachment,
        fingerprintsAttachment: FingerprintsAttachment?,
        signatureAttachment: SignatureAttachment?,
    ): Either<CodedError, Unit>

    protected fun processIdentityDocumentPage(
        enrolment: CompletedEnrolment,
        identityDocument: IdentityDocument,
        page: DocumentPage,
        pageSequenceNumber: Int,
    ): Either<CodedError, Unit> {
        return processDocumentPage(
            enrolment,
            identityDocument.documentAttachmentId,
            identityDocument.documentTypeCode,
            page,
            pageSequenceNumber
        )
    }

    private fun processSupportingDocumentPage(
        enrolment: CompletedEnrolment,
        supportingDocument: SupportingDocument,
        page: DocumentPage,
        pageSequenceNumber: Int
    ): Either<CodedError, Unit> {
        return processDocumentPage(
            enrolment,
            supportingDocument.documentAttachmentId,
            supportingDocument.documentTypeCode,
            page,
            pageSequenceNumber
        )
    }

    protected abstract fun processDocumentPage(
        enrolment: CompletedEnrolment,
        documentAttachmentId: DocumentAttachmentId,
        documentTypeCode: DocumentTypeCode,
        page: DocumentPage,
        pageSequenceNumber: Int
    ): Either<CodedError, Unit>

    private fun getAttachmentPage(pages: List<DocumentPage>, pageNumber: Int): Either<CodedError, DocumentPage> {
        return if (pageNumber > pages.size) {
            logger.error("invalid page number '$pageNumber' for attachment with '${pages.size}' pages")
            Either.left(EnrolmentErrors.INVALID_PAGE_NUMBER)
        } else {
            Either.right(pages[pageNumber - 1])
        }
    }

    private fun updateEnrolmentStatus(
        startedAt: LocalDateTime,
        task: EnrolmentOutboxTask,
        processingId: String,
        error: CodedError? = null
    ): Either<CodedError, Unit> {
        val completedAt = timeProvider.now()
        val enrolmentId = task.enrolment.enrolmentId
        enrolmentOutboxTaskRepository.createTaskResultAndUpdateTask(
            task.id,
            processingId,
            startedAt,
            timeProvider.now(),
            error
        )
        val elapsedTimeMillis = Duration.between(startedAt, completedAt).toMillis()
        return enrolmentRepository.updateAsProcessed(enrolmentId, error == null)
            .mapLeft {
                logger.error("updating enrolment processing status failed in ${elapsedTimeMillis}ms -- {}", it.message)
                it
            }
            .map { logger.info("updating enrolment processing status completed in ${elapsedTimeMillis}ms") }
    }
}