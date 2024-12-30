package civ.cnam.enrolment.domain.model.event

import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId

class PhotoAttached(
    val enrolmentId: EnrolmentId,
    val photoAttachmentId: PhotoAttachmentId,
)