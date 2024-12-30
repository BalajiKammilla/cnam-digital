package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.event.EnrolmentDetailsCreatedOrUpdated
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.service.EnrolmentWorkflow
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class CreateOrUpdateEnrolmentDetailsOperation(private val enrolmentWorkflow: EnrolmentWorkflow) {
    @POST
    @Path("/details")
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Start a new enrolment with enrolment details")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = CreateOrUpdateEnrolmentDetailsResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = CreateOrUpdateEnrolmentDetailsErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    fun createEnrolmentWithDetails(
        @Valid @RequestBody newEnrolment: EnrolmentDetailsData,
        @Context ctx: SecurityContext,
    ): Either<CodedError, EnrolmentDetailsCreatedOrUpdated> {
        return enrolmentWorkflow.createOrUpdateWithEnrolmentDetails(
            MobileNumber(ctx.userPrincipal.name),
            null,
            newEnrolment,
        )
    }

    @PUT
    @Path("/{enrolmentId}/details")
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Update enrolment details")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = CreateOrUpdateEnrolmentDetailsResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = CreateOrUpdateEnrolmentDetailsErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    fun updateEnrolmentWithDetails(
        @PathParam("enrolmentId") enrolmentId: String,
        @RequestBody enrolmentDetails: EnrolmentDetailsData,
        @Context ctx: SecurityContext,
    ): Either<CodedError, EnrolmentDetailsCreatedOrUpdated> {
        return enrolmentWorkflow.createOrUpdateWithEnrolmentDetails(
            MobileNumber(ctx.userPrincipal.name),
            EnrolmentId(enrolmentId),
            enrolmentDetails,
        )
    }

    class CreateOrUpdateEnrolmentDetailsResponse(
        data: EnrolmentDetailsCreatedOrUpdated
    ) : DefaultApiCompletedResponse<EnrolmentDetailsCreatedOrUpdated>(data)
    class CreateOrUpdateEnrolmentDetailsErrorResponse(error: CodedError) : DefaultApiErrorResponse<CodedError>(error)
}
