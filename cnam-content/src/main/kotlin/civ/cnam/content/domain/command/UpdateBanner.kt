package civ.cnam.content.domain.command

import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_ENABLED
import civ.cnam.content.domain.service.ContentStore
import dev.dry.common.error.CodedError
import dev.dry.common.error.CommonErrors
import dev.dry.common.function.Either
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import java.io.InputStream

@Singleton
@IfBuildProperty(name = CNAM_CONTENT_ENABLED, stringValue = "true")
class UpdateBanner(private val contentStore: ContentStore) {
    operator fun invoke(inputStream: InputStream, imageSize: Long): Either<UpdateBannerError, BannerUpdated> {
        return try {
            contentStore.storeBannerImage(inputStream, imageSize)
            Either.right(BannerUpdated)
        } catch (ex: Exception) {
            Either.left(UpdatedBannerFailed)
        }
    }

    sealed class UpdateBannerError(error: CodedError) : CodedError.DefaultCodedError(error)
    object UpdatedBannerFailed : UpdateBannerError(CommonErrors.SERVER_ERROR)

    object BannerUpdated
}