package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.model.query.enrolment.GetEnrolmentSubscription
import civ.cnam.enrolment.domain.model.query.enrolment.GetEnrolmentSubscription.EnrolmentSubscription
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
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
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/{enrolmentId}/details/subscription")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetSubscriptionDetailsOperation(private val getEnrolmentSubscription: GetEnrolmentSubscription) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Get subscription details for an enrolment")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetEnrolmentSubscriptionResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetEnrolmentSubscriptionErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @Context ctx: SecurityContext,
        @PathParam("enrolmentId") enrolmentId: String,
    ): Either<EnrolmentErrors.EnrolmentNotFoundError, EnrolmentSubscription> {
        return getEnrolmentSubscription(EnrolmentId(enrolmentId), MobileNumber(ctx.userPrincipal.name))
    }

    class GetEnrolmentSubscriptionResponse(
        data: EnrolmentSubscription
    ) : DefaultApiCompletedResponse<EnrolmentSubscription>(data)
    class GetEnrolmentSubscriptionErrorResponse(
        error: EnrolmentErrors.EnrolmentNotFoundError
    ) : DefaultApiErrorResponse<CodedError>(error)
}
