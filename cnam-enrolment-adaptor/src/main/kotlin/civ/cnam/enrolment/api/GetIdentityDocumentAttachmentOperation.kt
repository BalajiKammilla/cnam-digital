package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.query.attachment.LoadAttachment
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.IdentityDocumentAttachmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
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
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/{enrolmentId}/identity-document/{attachmentId}")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetIdentityDocumentAttachmentOperation(private val loadAttachment: LoadAttachment) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Retrieve identity document attachment")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetIdentityDocumentAttachmentResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetIdentityDocumentAttachmentErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    fun getPhotoAttachment(
        @PathParam("enrolmentId") enrolmentId: String,
        @PathParam("attachmentId") attachmentId: String,
    ): Either<CodedError, IdentityDocumentAttachment> {
        return loadAttachment(
            EnrolmentId(enrolmentId),
            IdentityDocumentAttachmentId(attachmentId),
        )
    }

    class GetIdentityDocumentAttachmentResponse(
        data: IdentityDocumentAttachment
    ) : DefaultApiCompletedResponse<IdentityDocumentAttachment>(data)
    class GetIdentityDocumentAttachmentErrorResponse(error: CodedError) : DefaultApiErrorResponse<CodedError>(error)
}
