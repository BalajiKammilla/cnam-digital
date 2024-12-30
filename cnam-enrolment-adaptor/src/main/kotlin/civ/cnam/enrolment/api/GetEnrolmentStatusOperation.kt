package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.command.GetEnrolmentStatus
import civ.cnam.enrolment.domain.command.GetEnrolmentStatus.EnrolmentStatus
import civ.cnam.enrolment.domain.command.GetEnrolmentStatus.GetEnrolmentStatusError
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.validation.constraints.Size
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/{enrolmentId}/status")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetEnrolmentStatusOperation(private val getEnrolmentStatus: GetEnrolmentStatus) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Retrieve enrolment status")
    @APIResponses(value = [
        APIResponse(
            responseCode = "200",
            content = [Content(schema = Schema(implementation = GetEnrolmentStatusResponse::class))]
        ),
        APIResponse(
            content = [Content(schema = Schema(implementation = GetEnrolmentStatusErrorResponse::class))]
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @Size(max = 36)
        @PathParam("enrolmentId") enrolmentId: String,
        @Context ctx: SecurityContext,
    ): Either<GetEnrolmentStatusError, EnrolmentStatus> {
        return getEnrolmentStatus(EnrolmentId(enrolmentId))
    }

    class GetEnrolmentStatusResponse(
        data: EnrolmentStatus
    ) : DefaultApiCompletedResponse<EnrolmentStatus>(data)

    class GetEnrolmentStatusErrorResponse(
        error: GetEnrolmentStatusError
    ) : DefaultApiErrorResponse<GetEnrolmentStatusError>(error)
}
