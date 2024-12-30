package civ.cnam.content.api

import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_API_ENABLED
import civ.cnam.content.constants.ContentBuildProperty.CNAM_CONTENT_ENABLED
import civ.cnam.content.domain.service.ContentStore
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Encoding
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import java.io.InputStream

@Path("/management/content/banner")
@Tags(Tag(name = "Content"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_CONTENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_CONTENT_API_ENABLED, stringValue = "true")
])
class GetBannerOperation(private val contentStore: ContentStore) {
    @GET
    @PermitAll
    @Operation(summary = "Get banner")
    @Produces(MULTIPART_FORM_DATA)
    @APIResponse(
        responseCode="200",
        content = [
            Content(
                mediaType = MULTIPART_FORM_DATA,
                schema = Schema(
                    title = "banner image",
                    format = "binary"
                ),
                encoding = [Encoding(name = "bannerImage", contentType = "image/png")]
            )
        ]
    )
    operator fun invoke(): RestResponse<InputStream> {
        return contentStore.loadBannerImage().fold(
            { error ->
                when(error) {
                    ContentStore.ContentNotFound -> ResponseBuilder.notFound<InputStream>()
                    ContentStore.ContentRetrievalFailed -> ResponseBuilder.serverError()
                }.build()
            },
            { banner ->
                val builder = ResponseBuilder.ok(banner.inputStream, "image/png")
                val contentLength = banner.contentLength
                if (contentLength != null) {
                    builder.header("Content-Length", contentLength)
                }
                builder.build()
            }
        )
    }
}
