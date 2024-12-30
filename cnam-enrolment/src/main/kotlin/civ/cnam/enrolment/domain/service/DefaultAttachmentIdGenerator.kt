package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import java.util.*

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class DefaultAttachmentIdGenerator : AttachmentIdGenerator {
    override fun generateDocumentAttachmentId(): DocumentAttachmentId = DocumentAttachmentId(generateId())
    override fun generatePhotoAttachmentId(): PhotoAttachmentId = PhotoAttachmentId(generateId())
    override fun generateFingerprintsAttachmentId(): FingerprintsAttachmentId = FingerprintsAttachmentId(generateId())
    override fun generateSignatureAttachmentId(): SignatureAttachmentId = SignatureAttachmentId(generateId())

    private fun generateId(): String = UUID.randomUUID().toString()
}
