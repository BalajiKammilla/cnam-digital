package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import civ.cnam.enrolment.domain.model.query.enrolment.FilterCorrectiveActions
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
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
@Tags(Tag(name = "Enrolment"))
@Path("/enrolment/{enrolmentId}/corrective-action")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetCorrectiveActionListOperation(private val filterCorrectiveActions: FilterCorrectiveActions) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(
        summary = "Get corrective actions for an enrolment",
        description = "Retrieves a paginated list of corrective actions for an enrolment",
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetReviewTaskListResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @PathParam("enrolmentId") enrolmentId: String,
        @QueryParam("status") status: CorrectiveActionStatus?,
        @Context ctx: SecurityContext,
    ): GetCorrectiveActionListData {
        val mobileNumber = MobileNumber(ctx.userPrincipal.name)
        val list = filterCorrectiveActions(EnrolmentId(enrolmentId), status, mobileNumber)
        return GetCorrectiveActionListData(list)
    }

    class GetCorrectiveActionListData(val list: List<CorrectiveAction>)

    class GetReviewTaskListResponse(
        data: GetCorrectiveActionListData
    ) : DefaultApiCompletedResponse<GetCorrectiveActionListData>(data)
}
