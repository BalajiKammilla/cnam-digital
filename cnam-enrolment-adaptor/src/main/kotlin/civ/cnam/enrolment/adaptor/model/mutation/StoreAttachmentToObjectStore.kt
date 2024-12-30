package civ.cnam.enrolment.adaptor.model.mutation

import civ.cnam.enrolment.adaptor.configuration.ObjectStoreNames
import civ.cnam.enrolment.adaptor.function.GenerateAttachmentObjectName
import civ.cnam.enrolment.adaptor.model.query.GetAttachmentFromObjectStore.AttachmentObject
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.mutation.attachment.StoreAttachment
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
import dev.dry.common.time.StopWatch
import dev.dry.core.objectstore.ObjectName
import dev.dry.core.objectstore.ObjectStore
import dev.dry.core.objectstore.ObjectStoreManager
import dev.dry.core.serialization.ObjectWriter
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.function.Supplier


@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class StoreAttachmentToObjectStore(
    private val objectWriter: ObjectWriter,
    objectStoreManagerSupplier: Supplier<ObjectStoreManager>
) : StoreAttachment {
    private val logger = LoggerFactory.getLogger(StoreAttachment::class.java)
    private val objectStore: ObjectStore = objectStoreManagerSupplier
        .get()
        .getOrCreate(ObjectStoreNames.ENROLMENT_OBJECT_STORE_NAME)

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: DocumentAttachmentId,
        attachment: IdentityDocumentAttachment,
    ) {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        val pagesSize = attachment.pages
            .map { it.image.size }
            .reduce { acc, current -> acc + current }
        val estimatedSize = AttachmentObject.ESTIMATED_DOCUMENT_ENVELOPE_SIZE + pagesSize
        logger.info(
            "storing attachment '$attachmentId' for enrolment '$enrolmentId' with estimated size $estimatedSize bytes"
        )
        store(objectName, attachmentId, attachment, estimatedSize)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: DocumentAttachmentId,
        attachment: DocumentAttachment,
    ) {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        val pagesSize = attachment.pages
            .map { it.image.size }
            .reduce { acc, current -> acc + current }
        val estimatedSize = AttachmentObject.ESTIMATED_DOCUMENT_ENVELOPE_SIZE + pagesSize
        logger.info(
            "storing attachment '$attachmentId' for enrolment '$enrolmentId' with estimated size $estimatedSize bytes"
        )
        store(objectName, attachmentId, attachment, estimatedSize)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: PhotoAttachmentId,
        attachment: PhotoAttachment,
    ) {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        val estimatedSize = AttachmentObject.ESTIMATED_PHOTO_ENVELOPE_SIZE + attachment.image.size
        logger.info(
            "storing portrait '$attachmentId' for enrolment '$enrolmentId' with estimated size $estimatedSize bytes"
        )
        store(objectName, attachmentId, attachment, estimatedSize)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: FingerprintsAttachmentId,
        attachment: FingerprintsAttachment,
    ) {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        val fingerprintsSize = attachment.fingerprints
            .map { it.image.size }
            .reduce { acc, current -> acc + current }
        val estimatedSize = AttachmentObject.ESTIMATED_FINGERPRINTS_ENVELOPE_SIZE + fingerprintsSize
        logger.info(
            "storing fingerprints '$attachmentId' for enrolment '$enrolmentId' with estimated size $estimatedSize bytes"
        )
        store(objectName, attachmentId, attachment, estimatedSize)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: SignatureAttachmentId,
        attachment: SignatureAttachment,
    ) {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        val estimatedSize = AttachmentObject.ESTIMATED_SIGNATURE_ENVELOPE_SIZE + attachment.image.size
        logger.info(
            "storing signature '$attachmentId' for enrolment '$enrolmentId' with estimated size $estimatedSize bytes"
        )
        store(objectName, attachmentId, attachment, estimatedSize)
    }

    private fun <ID, T : Any> store(
        objectName: ObjectName,
        attachmentId: ID,
        attachment: T,
        estimatedSize: Int,
    ) {
        val stopWatch = StopWatch.start()
        ByteArrayOutputStream(estimatedSize).use { outputStream ->
            objectWriter.write(outputStream, attachment)
            val objectBytes = outputStream.toByteArray()
            logger.info("serialised ${objectBytes.size} attachment object bytes in ${stopWatch.elapsedTimeMillis}ms")
            objectStore.put(objectName, ByteArrayInputStream(objectBytes))
            logger.info(
                "stored attachment '$attachmentId' of size ${objectBytes.size} bytes in ${stopWatch.elapsedTimeMillis}ms"
            )
        }
    }
}