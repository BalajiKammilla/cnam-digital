package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.query.referencedata.GetReferenceDatasets
import civ.cnam.enrolment.domain.model.query.referencedata.GetReferenceDatasets.ReferenceDatasets
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/reference-data")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetReferenceDatasetsOperation(private val getReferenceDatasets: GetReferenceDatasets) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Get reference datasets")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = GetPhotoAttachmentResponse::class)
        )
    ])
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(): ReferenceDatasets {
        return getReferenceDatasets()
    }

    class GetPhotoAttachmentResponse(data: ReferenceDatasets) : DefaultApiCompletedResponse<ReferenceDatasets>(data)
}
