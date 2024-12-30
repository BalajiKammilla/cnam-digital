package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId

interface AttachmentIdGenerator {
    fun generateDocumentAttachmentId(): DocumentAttachmentId
    fun generatePhotoAttachmentId(): PhotoAttachmentId
    fun generateFingerprintsAttachmentId(): FingerprintsAttachmentId
    fun generateSignatureAttachmentId(): SignatureAttachmentId
}