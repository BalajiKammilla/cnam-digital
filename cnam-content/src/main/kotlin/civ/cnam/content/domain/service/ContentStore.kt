package civ.cnam.content.domain.service

import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_ENABLED
import dev.dry.common.error.CodedError
import dev.dry.common.error.CommonErrors
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.common.function.mapLeft
import dev.dry.core.objectstore.ObjectName
import dev.dry.core.objectstore.ObjectStore
import dev.dry.core.objectstore.ObjectStoreManager
import dev.dry.core.objectstore.ObjectStoreName
import dev.dry.core.objectstore.error.ObjectNotFound
import dev.dry.core.objectstore.error.ObjectReadFailed
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.function.Supplier

@Singleton
@IfBuildProperty(name = CNAM_CONTENT_ENABLED, stringValue = "true")
class ContentStore(
    objectStoreManagerSupplier: Supplier<ObjectStoreManager>
) {
    private val objectStore: ObjectStore = objectStoreManagerSupplier.get().getOrCreate(CONTENT_OBJECT_STORE_NAME)

    fun loadBannerImage(): Either<GetContentError, Content> {
        return objectStore.get(BANNER_OBJECT_NAME)
            .mapLeft { error ->
                logger.warn("failed to retrieve banner - $error")
                when(error) {
                    is ObjectNotFound -> ContentNotFound
                    is ObjectReadFailed -> ContentRetrievalFailed
                }
            }.map { response ->
                Content(
                    inputStream = response.content,
                    contentType = response.contentType,
                    contentLength = response.contentLength
                )
            }
    }

    fun storeBannerImage(inputStream: InputStream, imageSize: Long) {
        objectStore.put(BANNER_OBJECT_NAME, inputStream, objectSize = imageSize)
    }

    sealed class GetContentError(error: CodedError) : CodedError.DefaultCodedError(error)
    object ContentNotFound : GetContentError(CommonErrors.NOT_FOUND)
    object ContentRetrievalFailed : GetContentError(CommonErrors.SERVER_ERROR)

    class Content(
        val inputStream: InputStream,
        val contentType: String?,
        val contentLength: Long?
    )

    companion object {
        private val logger = LoggerFactory.getLogger(ContentStore::class.java)

        private val BANNER_OBJECT_NAME = ObjectName("banner.png")

        const val CONTENT_OBJECT_STORE_NAME_VALUE = "content"

        val CONTENT_OBJECT_STORE_NAME = ObjectStoreName(CONTENT_OBJECT_STORE_NAME_VALUE)
    }
}