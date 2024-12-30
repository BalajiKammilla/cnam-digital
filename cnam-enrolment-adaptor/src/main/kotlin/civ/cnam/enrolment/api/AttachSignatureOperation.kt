package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.event.SignatureAttached
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
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
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
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
class AttachSignatureOperation(private val enrolmentWorkflow: EnrolmentWorkflow) {
    @POST
    @Path("/signature")
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Start a new enrolment with a signature attachment")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = AttachSignatureResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = AttachSignatureErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    fun createEnrolmentWithSignatureAttachment(
        @RequestBody signatureAttachment: SignatureAttachment,
        @Context ctx: SecurityContext,
    ): Either<CodedError, SignatureAttached> {
        return enrolmentWorkflow.createOrUpdateSignatureAttachment(
            MobileNumber(ctx.userPrincipal.name),
            null,
            signatureAttachment,
        )
    }

    @POST
    @Path("/{enrolmentId}/signature")
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Attach a signature to an existing enrolment")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = AttachSignatureResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = AttachSignatureErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    fun updateEnrolmentWithSignatureAttachment(
        @PathParam("enrolmentId") enrolmentId: String,
        @RequestBody signatureAttachment: SignatureAttachment,
        @Context ctx: SecurityContext,
    ): Either<CodedError, SignatureAttached> {
        return enrolmentWorkflow.createOrUpdateSignatureAttachment(
            MobileNumber(ctx.userPrincipal.name),
            EnrolmentId(enrolmentId),
            signatureAttachment,
        )
    }

    class AttachSignatureResponse(
        data: SignatureAttached
    ) : DefaultApiCompletedResponse<SignatureAttached>(data)
    class AttachSignatureErrorResponse(error: CodedError) : DefaultApiErrorResponse<CodedError>(error)
}
