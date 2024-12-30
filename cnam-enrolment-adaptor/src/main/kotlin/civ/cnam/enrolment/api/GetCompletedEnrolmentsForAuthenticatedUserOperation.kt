package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/completed")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetCompletedEnrolmentsForAuthenticatedUserOperation(private val enrolmentRepository: EnrolmentRepository) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Get completed enrolments for the authenticated user")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetCompletedEnrolmentsForAuthenticatedUserResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(@Context ctx: SecurityContext): List<EnrolmentListItem> {
        return enrolmentRepository.findCompletedEnrolments(MobileNumber(ctx.userPrincipal.name))
    }

    class GetCompletedEnrolmentsForAuthenticatedUserResponse(
        data: List<EnrolmentListItem>
    ) : DefaultApiCompletedResponse<List<EnrolmentListItem>>(data)
}
