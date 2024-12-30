package civ.cnam.enrolment.domain.model.query.attachment

import civ.cnam.enrolment.domain.model.type.attachment.FingerprintImage
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId

interface GetFingerprintImages {
    operator fun invoke(enrolmentId: EnrolmentId, attachmentId: FingerprintsAttachmentId):
            List<FingerprintImage>
}
