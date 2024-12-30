package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors.CannotUpdateCompletedEnrolment
import civ.cnam.enrolment.domain.error.EnrolmentErrors.CorrectiveActionNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.function.DetermineRequiredSupportingDocuments
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType
import civ.cnam.enrolment.domain.model.event.DocumentAttached
import civ.cnam.enrolment.domain.model.event.EnrolmentDetailsCreatedOrUpdated
import civ.cnam.enrolment.domain.model.event.FingerprintsAttached
import civ.cnam.enrolment.domain.model.event.PhotoAttached
import civ.cnam.enrolment.domain.model.event.SignatureAttached
import civ.cnam.enrolment.domain.model.mutation.attachment.StoreAttachment
import civ.cnam.enrolment.domain.model.mutation.enrolment.CompleteCorrectiveAction
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.function.flatten
import dev.dry.common.function.map
import dev.dry.core.data.model.value.MobileNumber
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentWorkflow(
    private val enrolmentRepository: EnrolmentRepository,
    private val completeCorrectiveAction: CompleteCorrectiveAction,
    private val createReviewTask: CreateReviewTask,
    private val determineRequiredSupportingDocuments: DetermineRequiredSupportingDocuments,
    private val attachmentIdGenerator: AttachmentIdGenerator,
    private val storeAttachment: StoreAttachment,
) {
    private val logger = LoggerFactory.getLogger(EnrolmentWorkflow::class.java)

    @Transactional
    fun createOrUpdateWithIdentityDocumentAttachment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        identityDocumentAttachment: IdentityDocumentAttachment,
    ): Either<CodedError, DocumentAttached> {
        logger.info(
            "create or update enrolment '{}' with identity document attachment (documentType: '{}')",
            enrolmentId,
            identityDocumentAttachment.documentTypeCode,
        )

        val documentAttachmentId = attachmentIdGenerator.generateDocumentAttachmentId()

        return createOrUpdate(mobileNumber, enrolmentId) { enrolment ->
            if (enrolment.completed) {
                completeCorrectiveAction(enrolment.id, CorrectiveActionType.IDENTITY_DOCUMENT)
                    .fold({ return@createOrUpdate left(it) }, {})

                createReviewTask(enrolment.id, ReviewTaskType.IDENTITY_DOCUMENT)
                    .fold({ return@createOrUpdate left(CorrectiveActionNotFound) }, {})
            }

            val identityDocument = enrolmentRepository.createOrUpdateIdentityDocument(
                enrolmentId = enrolment.enrolmentId,
                documentNumber = identityDocumentAttachment.documentNumber,
                documentTypeCode = identityDocumentAttachment.documentTypeCode,
                documentAttachmentId = documentAttachmentId,
                pageCount = identityDocumentAttachment.pages.size,
                ocrSucceeded = identityDocumentAttachment.ocrSucceeded,
            ).fold({ return@createOrUpdate left(it) }, { it })

            storeAttachment(enrolment.enrolmentId, identityDocument.documentAttachmentId, identityDocumentAttachment)

            right(DocumentAttached(enrolment.enrolmentId, identityDocument.documentAttachmentId))
        }.flatten()
    }

    @Transactional
    fun updateWithDocumentAttachment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId,
        documentAttachment: DocumentAttachment,
    ): Either<CodedError, DocumentAttached> {
        return createOrUpdate(mobileNumber, enrolmentId) { enrolment ->
            if (enrolment.completed) {
                alreadyCompleted()
            } else {
                createOrReplaceSupportingDocument(enrolment.enrolmentId, documentAttachment)
            }
        }.flatten()
    }

    @Transactional
    fun createOrUpdateWithEnrolmentDetails(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        enrolmentDetails: EnrolmentDetailsData,
    ): Either<CodedError, EnrolmentDetailsCreatedOrUpdated> {
        logger.info("create or update enrolment '$enrolmentId' with details")

        val enrolment = enrolmentRepository.createOrUpdatePartialEnrolment(
            mobileNumber,
            enrolmentId,
            enrolmentDetails = enrolmentDetails
        ).fold({ return left(it) }, { it })

        return if (enrolment.completed) {
            alreadyCompleted()
        } else {
            val supportingDocuments = determineRequiredSupportingDocuments(enrolmentDetails)
            val supportingDocumentPurposes = supportingDocuments.map { sd -> sd.purpose }
            logger.info(
                "create or update enrolment '{}' with required supporting document purposes {}",
                enrolment.enrolmentId,
                supportingDocumentPurposes,
            )
            right(EnrolmentDetailsCreatedOrUpdated(enrolment.enrolmentId, supportingDocuments))
        }
    }

    @Transactional
    fun createOrUpdatePhotoAttachment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        photoAttachment: PhotoAttachment,
    ): Either<CodedError, PhotoAttached> {
        logger.info("create or update enrolment '$enrolmentId' with photo attachment")
        val photoAttachmentId = attachmentIdGenerator.generatePhotoAttachmentId()
        return createOrUpdate(
            mobileNumber,
            enrolmentId,
            photoAttachmentId = photoAttachmentId,
        ) {
            if (it.completed) {
                alreadyCompleted()
            } else {
                storeAttachment(it.enrolmentId, photoAttachmentId, photoAttachment)
                right(PhotoAttached(it.enrolmentId, photoAttachmentId))
            }
        }.flatten()
    }

    @Transactional
    fun createOrUpdateFingerprintsAttachment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        fingerprintsAttachment: FingerprintsAttachment,
    ): Either<CodedError, FingerprintsAttached> {
        logger.info("create or update enrolment '$enrolmentId' with fingerprints attachment")
        val fingerprintsAttachmentId = attachmentIdGenerator.generateFingerprintsAttachmentId()
        return createOrUpdate(
            mobileNumber,
            enrolmentId,
            fingerprintsAttachmentId = fingerprintsAttachmentId,
        ) {
            if (it.completed) {
                alreadyCompleted()
            } else {
                storeAttachment(it.enrolmentId, fingerprintsAttachmentId, fingerprintsAttachment)
                right(FingerprintsAttached(it.enrolmentId, fingerprintsAttachmentId))
            }
        }.flatten()
    }

    @Transactional
    fun createOrUpdateSignatureAttachment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        signatureAttachment: SignatureAttachment,
    ): Either<CodedError, SignatureAttached> {
        logger.info("create or update enrolment '$enrolmentId' with signature attachment")
        val signatureAttachmentId = attachmentIdGenerator.generateSignatureAttachmentId()
        return createOrUpdate(
            mobileNumber,
            enrolmentId,
            signatureAttachmentId = signatureAttachmentId,
        ) {
            if (it.completed) {
                alreadyCompleted()
            } else {
                storeAttachment(it.enrolmentId, signatureAttachmentId, signatureAttachment)
                right(SignatureAttached(it.enrolmentId, signatureAttachmentId))
            }
        }.flatten()
    }

    fun createOrReplaceSupportingDocument(
        enrolmentId: EnrolmentId,
        documentAttachment: DocumentAttachment,
    ): Either<EnrolmentNotFound, DocumentAttached> {
        logger.info(
            "update enrolment '{}' with document attachment (purpose: '{}', documentType: '{}')",
            enrolmentId,
            documentAttachment.purpose,
            documentAttachment.documentTypeCode,
        )
        val documentAttachmentId = attachmentIdGenerator.generateDocumentAttachmentId()
        return enrolmentRepository.createOrReplaceSupportingDocument(
            enrolmentId,
            documentAttachmentId,
            documentAttachment.purpose,
            documentAttachment.documentTypeCode,
            documentAttachment.pages.size
        ).map {
            storeAttachment(enrolmentId, documentAttachmentId, documentAttachment)
            logger.info(
                "attached supporting document '$documentAttachmentId' of type '${documentAttachment.documentTypeCode}'"
            )
            DocumentAttached(enrolmentId, documentAttachmentId)
        }
    }

    private fun <T> createOrUpdate(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        fingerprintsAttachmentId: FingerprintsAttachmentId? = null,
        photoAttachmentId: PhotoAttachmentId? = null,
        signatureAttachmentId: SignatureAttachmentId? = null,
        enrolmentDetails: EnrolmentDetailsData? = null,
        putObject: (PartialEnrolment) -> T,
    ): Either<CodedError, T> {
        return enrolmentRepository.createOrUpdatePartialEnrolment(
            mobileNumber,
            enrolmentId,
            fingerprintsAttachmentId = fingerprintsAttachmentId,
            photoAttachmentId = photoAttachmentId,
            signatureAttachmentId = signatureAttachmentId,
            enrolmentDetails = enrolmentDetails,
        ).map(putObject)
    }

    companion object {
        private fun alreadyCompleted(): Either.Left<CannotUpdateCompletedEnrolment> {
            return left(CannotUpdateCompletedEnrolment)
        }
    }
}

