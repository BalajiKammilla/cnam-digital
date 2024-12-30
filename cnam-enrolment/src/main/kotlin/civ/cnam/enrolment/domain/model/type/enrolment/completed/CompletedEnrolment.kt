package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.ExternalReference
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import dev.dry.core.data.model.value.MobileNumber
import java.time.LocalDateTime

class CompletedEnrolment(
    val mobileNumber: MobileNumber,
    val enrolmentId: EnrolmentId,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime,
    val verifiedAt: LocalDateTime?,
    val processingCompletedAt: LocalDateTime?,
    val approvalStatus: PartialEnrolment.ApprovalStatus?,
    val identityDocument: IdentityDocument,
    val fingerprintsAttachmentId: FingerprintsAttachmentId?,
    val photoAttachmentId: PhotoAttachmentId,
    val signatureAttachmentId: SignatureAttachmentId?,
    val supportingDocuments: List<SupportingDocument>,
    val enrolmentDetails: EnrolmentDetails,
    val externalReference: ExternalReference?,
)
