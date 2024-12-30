package civ.cnam.enrolment.domain.model.event

import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId

class DocumentAttached(
    val enrolmentId: EnrolmentId,
    val documentAttachmentId: DocumentAttachmentId,
)
