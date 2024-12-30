package civ.cnam.content.management.api

import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_ENABLED
import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_MANAGEMENT_API_ENABLED
import civ.cnam.content.constants.ContentPermissionValue.CONTENT_UPDATE_VALUE
import civ.cnam.content.domain.service.ContentStore
import dev.dry.common.error.CodedError
import dev.dry.common.error.CodedError.DefaultCodedError
import dev.dry.common.error.CommonErrors
import dev.dry.common.function.Either
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.inputStream
import java.nio.file.Path as FilePath

@Path("/management/content/banner")
@Tags(Tag(name = "Content Management"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_CONTENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_CONTENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class UpdateBannerOperation(private val contentStore: ContentStore) {
    @PUT
    @PermissionsAllowed(value = [CONTENT_UPDATE_VALUE])
    @Operation(summary = "Update banner")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(@RestForm("bannerImage") bannerImage: FileUpload): Either<CodedError, UploadBannerResponse> {
        return try {
            val inputStream = bannerImage.uploadedFile().inputStream()
            contentStore.storeBannerImage(inputStream, bannerImage.size())
            Either.right(UploadBannerResponse())
        } catch (ex: IOException) {
            Either.left(FileUploadFailedError)
        }
    }

    companion object {
        val UPLOADED_FILE_PATH: FilePath = Paths.get("/Users/vg/Dev/Aptiway/CNAM/banner-uploads/banner.png")
    }

    sealed class UploadBannerError(error: CodedError) : DefaultCodedError(error)
    object FileNotFoundInRequestError : UploadBannerError(CommonErrors.BAD_REQUEST)
    object FileUploadFailedError : UploadBannerError(CommonErrors.SERVER_ERROR)

    class UploadBannerResponse
}
