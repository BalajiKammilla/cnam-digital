package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_READ_VALUE
import civ.cnam.enrolment.domain.model.query.attachment.GetFingerprintImages
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintImage
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/management/enrolment/{enrolmentId}/fingerprints/{attachmentId}")
@Tags(Tag(name = "Enrolment Management"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetFingerprintImagesOperation(
    private val getFingerprintImages: GetFingerprintImages
) {
    @GET
    @PermissionsAllowed(value = [ENROLMENT_READ_VALUE])
    @Operation(summary = "Retrieve fingerprint images")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetFingerprintAttachmentResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    fun getFingerprintAttachment(
        @PathParam("enrolmentId") enrolmentId: String,
        @PathParam("attachmentId") attachmentId: String
    ): List<FingerprintImage> {
        return getFingerprintImages(EnrolmentId(enrolmentId), FingerprintsAttachmentId(attachmentId))
    }
    class GetFingerprintAttachmentResponse(
        data: List<FingerprintImage>
    ): DefaultApiCompletedResponse<List<FingerprintImage>>(data)
}
