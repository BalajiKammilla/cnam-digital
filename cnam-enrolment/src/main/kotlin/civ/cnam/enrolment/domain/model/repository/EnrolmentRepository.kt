package civ.cnam.enrolment.domain.model.repository

import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.data.pagination.Page
import java.time.LocalDateTime

interface EnrolmentRepository {
    fun createPartialEnrolment(
        mobileNumber: MobileNumber,
        fingerprintsAttachmentId: FingerprintsAttachmentId? = null,
        photoAttachmentId: PhotoAttachmentId? = null,
        signatureAttachmentId: SignatureAttachmentId? = null,
        enrolmentDetails: EnrolmentDetailsData? = null,
    ): PartialEnrolment

    fun updatePartialEnrolment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId,
        fingerprintsAttachmentId: FingerprintsAttachmentId? = null,
        photoAttachmentId: PhotoAttachmentId? = null,
        signatureAttachmentId: SignatureAttachmentId? = null,
        enrolmentDetails: EnrolmentDetailsData? = null,
    ): Either<EnrolmentNotFound, PartialEnrolment>

    fun createOrUpdatePartialEnrolment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId?,
        fingerprintsAttachmentId: FingerprintsAttachmentId? = null,
        photoAttachmentId: PhotoAttachmentId? = null,
        signatureAttachmentId: SignatureAttachmentId? = null,
        enrolmentDetails: EnrolmentDetailsData? = null,
    ): Either<CodedError, PartialEnrolment> {
        return if (enrolmentId == null) {
            Either.right(
                createPartialEnrolment(
                    mobileNumber,
                    fingerprintsAttachmentId,
                    photoAttachmentId,
                    signatureAttachmentId,
                    enrolmentDetails,
                )
            )
        } else {
            updatePartialEnrolment(
                mobileNumber,
                enrolmentId,
                fingerprintsAttachmentId,
                photoAttachmentId,
                signatureAttachmentId,
                enrolmentDetails,
            )
        }
    }

    fun findPartialEnrolmentOrNull(enrolmentId: EnrolmentId): PartialEnrolment?

    fun findPartialEnrolment(enrolmentId: EnrolmentId): Either<EnrolmentNotFound, PartialEnrolment>

    fun findPartialEnrolment(enrolmentId: PartialEnrolment.ID): Either<EnrolmentNotFound, PartialEnrolment>

    fun findPartialEnrolment(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber
    ): Either<EnrolmentErrors.EnrolmentNotFoundError, PartialEnrolment>

    fun findEnrolmentsPendingProcessing(pageNumber: Int, pageSize: Int): Page<EnrolmentId>

    fun findCompletedEnrolments(mobileNumber: MobileNumber): List<EnrolmentListItem>

    fun findByFilter(filter: EnrolmentFilter, pageNumber: Int, pageSize: Int): Page<PartialEnrolment>

    fun updateAsProcessed(
        enrolmentId: EnrolmentId,
        status: Boolean,
    ): Either<CodedError, Unit>

    fun updateCompletedAt(enrolmentId: EnrolmentId, completedAt: LocalDateTime): Either<EnrolmentNotFound, Unit>

    fun createOrUpdateIdentityDocument(
        enrolmentId: EnrolmentId,
        documentNumber: DocumentNumber,
        documentTypeCode: DocumentTypeCode,
        documentAttachmentId: DocumentAttachmentId,
        pageCount: Int,
        ocrSucceeded: Boolean,
    ): Either<EnrolmentNotFound, IdentityDocument>

    fun createOrReplaceSupportingDocument(
        enrolmentId: EnrolmentId,
        documentAttachmentId: DocumentAttachmentId,
        purpose: SupportingDocument.Purpose,
        documentTypeCode: DocumentTypeCode,
        pageCount: Int
    ): Either<EnrolmentNotFound, SupportingDocument>

    fun findDedupeMatches(enrolmentId: EnrolmentId): Either<CodedError, List<CompletedEnrolment>>

    fun updateApprovalStatus(
        enrolmentId: EnrolmentId,
        approvalStatus: ApprovalStatus,
    ): Either<CodedError, Unit>
}
