package civ.cnam.enrolment.domain.model.event

import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId

class FingerprintsAttached(
    val enrolmentId: EnrolmentId,
    val fingerprintsAttachmentId: FingerprintsAttachmentId
)