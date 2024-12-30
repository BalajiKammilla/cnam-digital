package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.entity.SupportingDocument.Purpose
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.ExternalReference
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface PartialEnrolment {
    val id: ID
    val mobileNumber: MobileNumber
    val enrolmentId: EnrolmentId
    val startedAt: LocalDateTime
    val completedAt: LocalDateTime?
    val completed: Boolean get() = completedAt != null
    val verifiedAt: LocalDateTime?
    val verificationTaskPendingCount: Int
    val verificationTasksPending: Boolean get() = verificationTaskPendingCount > 0
    val correctiveActionPendingCount: Int
    val correctiveActionsPending: Boolean get() = correctiveActionPendingCount > 0
    val reviewTaskPendingCount: Int
    val reviewTasksPending: Boolean get() = reviewTaskPendingCount > 0
    val approvalStatus: ApprovalStatus?
    val processedAt: LocalDateTime?
    val processed: Boolean get() = processedAt != null
    val dedupeMatch: DedupeMatch?
    val identityDocument: IdentityDocument?
    val fingerprintsAttachmentId: FingerprintsAttachmentId?
    val photoAttachmentId: PhotoAttachmentId?
    val signatureAttachmentId: SignatureAttachmentId?
    val supportingDocuments: List<SupportingDocument>
    val enrolmentDetails: EnrolmentDetails?
    val externalReference: ExternalReference?

    val actions: List<EnrolmentAction>

    val verificationChecks: List<VerificationOutboxTask>

    val reviewTasks: List<ReviewTask>

    val correctiveActions: List<CorrectiveAction>

    val requiredSupportingDocuments: List<Purpose>? get() = this.enrolmentDetails?.requiredSupportingDocuments

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        companion object {
            val NULL = ID(0)
        }
    }

    enum class ApprovalStatus { PENDING, REJECTED, APPROVED }
}
