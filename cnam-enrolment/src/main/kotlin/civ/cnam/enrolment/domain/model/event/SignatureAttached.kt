package civ.cnam.enrolment.domain.model.event

import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId

class SignatureAttached(
    val enrolmentId: EnrolmentId,
    val signatureAttachmentId: SignatureAttachmentId,
)