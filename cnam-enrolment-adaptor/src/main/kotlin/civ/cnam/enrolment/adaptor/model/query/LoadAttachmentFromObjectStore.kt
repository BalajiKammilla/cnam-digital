package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.configuration.ObjectStoreNames
import civ.cnam.enrolment.adaptor.function.GenerateAttachmentObjectName
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.model.query.attachment.LoadAttachment
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
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.common.time.StopWatch
import dev.dry.core.objectstore.ObjectName
import dev.dry.core.objectstore.ObjectStore
import dev.dry.core.objectstore.ObjectStoreManager
import dev.dry.core.serialization.ObjectReader
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import kotlin.reflect.KClass

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class LoadAttachmentFromObjectStore(
    private val objectReader: ObjectReader,
    objectStoreManagerSupplier: Supplier<ObjectStoreManager>
) : LoadAttachment {
    private val logger = LoggerFactory.getLogger(LoadAttachmentFromObjectStore::class.java)
    private val objectStore: ObjectStore = objectStoreManagerSupplier
        .get()
        .getOrCreate(ObjectStoreNames.ENROLMENT_OBJECT_STORE_NAME)

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: IdentityDocumentAttachmentId
    ): Either<CodedError, IdentityDocumentAttachment> {
        val objectName = GenerateAttachmentObjectName(enrolmentId, DocumentAttachmentId(attachmentId.value))
        logger.info("retrieving document '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, IdentityDocumentAttachment::class)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: DocumentAttachmentId
    ): Either<CodedError, DocumentAttachment> {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving document '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, DocumentAttachment::class)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: PhotoAttachmentId
    ): Either<CodedError, PhotoAttachment> {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving photo '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, PhotoAttachment::class)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: FingerprintsAttachmentId
    ): Either<CodedError, FingerprintsAttachment> {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving fingerprints '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, FingerprintsAttachment::class)
    }

    override operator fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: SignatureAttachmentId
    ): Either<CodedError, SignatureAttachment> {
        val objectName = GenerateAttachmentObjectName(enrolmentId, attachmentId)
        logger.info("retrieving signature '$attachmentId' for enrolment '$enrolmentId'")
        return getAttachment(objectName, SignatureAttachment::class)
    }

    private fun <T : Any> getAttachment(
        objectName: ObjectName,
        objectType: KClass<T>,
    ): Either<CodedError, T> {
        logger.info("retrieving attachment '$objectName'")
        val stopWatch = StopWatch.start()
        return try {
            objectStore.get(objectName).map {
                logger.info("retrieved attachment object in ${stopWatch.elapsedTimeMillis}ms")
                objectReader.read(it.content, objectType).also {
                    logger.info("deserialized attachment object in ${stopWatch.elapsedTimeMillis}ms")
                }
            }
        } catch (ex: Exception) {
            Either.left(EnrolmentErrors.ATTACHMENT_RETRIEVAL_FAILED)
        }
    }
}