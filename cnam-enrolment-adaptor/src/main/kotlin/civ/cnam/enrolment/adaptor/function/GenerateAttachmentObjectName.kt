package civ.cnam.enrolment.adaptor.function

import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.AttachmentType
import civ.cnam.enrolment.domain.model.value.attachment.AttachmentType.DOCUMENT
import civ.cnam.enrolment.domain.model.value.attachment.AttachmentType.FINGERPRINTS
import civ.cnam.enrolment.domain.model.value.attachment.AttachmentType.PHOTO
import civ.cnam.enrolment.domain.model.value.attachment.AttachmentType.SIGNATURE
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.IdentityDocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import dev.dry.core.objectstore.ObjectName

object GenerateAttachmentObjectName {
    private fun generate(
        enrolmentId: EnrolmentId,
        attachmentId: String,
        attachmentType: AttachmentType,
    ): ObjectName {
        val aa = enrolmentId.value.substring(0..1)
        val bb = enrolmentId.value.substring(2..3)
        return ObjectName("$aa/$bb/${enrolmentId.value}/$attachmentId.${attachmentType.name.lowercase()}.json")
    }

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: IdentityDocumentAttachmentId): ObjectName =
        generate(enrolmentId, attachmentId.value, DOCUMENT)

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: DocumentAttachmentId): ObjectName =
        generate(enrolmentId, attachmentId.value, DOCUMENT)

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: PhotoAttachmentId): ObjectName =
        generate(enrolmentId, attachmentId.value, PHOTO)

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: FingerprintsAttachmentId): ObjectName =
        generate(enrolmentId, attachmentId.value, FINGERPRINTS)

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: SignatureAttachmentId): ObjectName =
        generate(enrolmentId, attachmentId.value, SIGNATURE)
}
