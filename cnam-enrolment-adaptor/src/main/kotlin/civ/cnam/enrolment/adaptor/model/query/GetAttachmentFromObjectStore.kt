package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.configuration.ObjectStoreNames
import civ.cnam.enrolment.adaptor.function.GenerateAttachmentObjectName
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.mutation.attachment.StoreAttachment
import civ.cnam.enrolment.domain.model.query.attachment.GetAttachment
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
import dev.dry.common.time.StopWatch
import dev.dry.core.objectstore.ObjectName
import dev.dry.core.objectstore.ObjectStore
import dev.dry.core.objectstore.ObjectStoreManager
import dev.dry.core.serialization.ObjectReader
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.function.Supplier
import kotlin.reflect.KClass

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetAttachmentFromObjectStore(
    private val objectReader: ObjectReader,
    objectStoreManagerSupplier: Supplier<ObjectStoreManager>
) : GetAttachment {
    private val objectStore: ObjectStore = objectStoreManagerSupplier
        .get()
        .getOrCreate(ObjectStoreNames.ENROLMENT_OBJECT_STORE_NAME)
    object AttachmentObject {
        const val ESTIMATED_DOCUMENT_ENVELOPE_SIZE = 256
        const val ESTIMATED_DOCUMENT_SIZE = ESTIMATED_DOCUMENT_ENVELOPE_SIZE + (1024 * 2)
        const val ESTIMATED_PHOTO_ENVELOPE_SIZE = 256
        const val ESTIMATED_PHOTO_SIZE = ESTIMATED_PHOTO_ENVELOPE_SIZE + 1024
        const val ESTIMATED_FINGERPRINTS_ENVELOPE_SIZE = 256
        const val ESTIMATED_FINGERPRINTS_SIZE = ESTIMATED_FINGERPRINTS_ENVELOPE_SIZE + (256 * 10)
        const val ESTIMATED_SIGNATURE_ENVELOPE_SIZE = 256
        const val ESTIMATED_SIGNATURE_SIZE = ESTIMATED_SIGNATURE_ENVELOPE_SIZE + 1024 * 1
    }

    private val logger = LoggerFactory.getLogger(StoreAttachment::class.java)

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: IdentityDocumentAttachmentId
    ): IdentityDocumentAttachment {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving document '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, IdentityDocumentAttachment::class, AttachmentObject.ESTIMATED_DOCUMENT_SIZE)
    }

    override operator fun invoke(enrolmentId: EnrolmentId, attachmentId: DocumentAttachmentId): DocumentAttachment {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving document '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, DocumentAttachment::class, AttachmentObject.ESTIMATED_DOCUMENT_SIZE)
    }

    override operator fun invoke(enrolmentId: EnrolmentId, attachmentId: PhotoAttachmentId): PhotoAttachment {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving photo '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, PhotoAttachment::class, AttachmentObject.ESTIMATED_PHOTO_SIZE)
    }

    override operator fun invoke(enrolmentId: EnrolmentId, attachmentId: FingerprintsAttachmentId): FingerprintsAttachment {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving fingerprints '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, FingerprintsAttachment::class, AttachmentObject.ESTIMATED_FINGERPRINTS_SIZE)
    }

    override operator fun invoke(enrolmentId: EnrolmentId, attachmentId: SignatureAttachmentId): SignatureAttachment {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving signature '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, SignatureAttachment::class, AttachmentObject.ESTIMATED_SIGNATURE_SIZE)
    }

    private fun <T : Any> getAttachment(objectName: ObjectName, objectType: KClass<T>, estimatedSize: Int): T {
        logger.info("retrieving attachment '$objectName'")
        val stopWatch = StopWatch.start()
        ByteArrayOutputStream(estimatedSize).use { outputStream ->
            objectStore.get(objectName, outputStream)
            val objectBytes = outputStream.toByteArray()
            logger.info(
                "retrieved attachment object of ${objectBytes.size} bytes in ${stopWatch.elapsedTimeMillis}ms"
            )
            return objectReader.read(ByteArrayInputStream(objectBytes), objectType).also {
                logger.info(
                    "deserialized attachment object of ${objectBytes.size} bytes in ${stopWatch.elapsedTimeMillis}ms"
                )
            }
        }
    }
}