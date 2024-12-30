package civ.cnam.enrolment.domain.model.mutation.attachment

import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId

interface StoreAttachment {
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: DocumentAttachmentId, attachment: IdentityDocumentAttachment)
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: DocumentAttachmentId, attachment: DocumentAttachment)
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: PhotoAttachmentId, attachment: PhotoAttachment)
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: FingerprintsAttachmentId, attachment: FingerprintsAttachment)
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: SignatureAttachmentId, attachment: SignatureAttachment)
}
