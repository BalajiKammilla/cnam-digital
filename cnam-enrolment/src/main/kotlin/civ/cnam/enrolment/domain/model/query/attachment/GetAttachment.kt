package civ.cnam.enrolment.domain.model.query.attachment

import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.IdentityDocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId

interface GetAttachment {
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: IdentityDocumentAttachmentId): IdentityDocumentAttachment

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: DocumentAttachmentId): DocumentAttachment

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: PhotoAttachmentId): PhotoAttachment

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: FingerprintsAttachmentId): FingerprintsAttachment

    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: SignatureAttachmentId): SignatureAttachment
}

