package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.query.attachment.LoadAttachment
import civ.cnam.enrolment.domain.model.type.attachment.DocumentAttachment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
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
@Path("/enrolment/{enrolmentId}/document/{attachmentId}")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetDocumentAttachmentOperation(private val loadAttachment: LoadAttachment) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Retrieve document attachment")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetDocumentAttachmentResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    fun getPhotoAttachment(
        @PathParam("enrolmentId") enrolmentId: String,
        @PathParam("attachmentId") attachmentId: String,
    ): Either<CodedError, DocumentAttachment> {
        return loadAttachment(
            EnrolmentId(enrolmentId),
            DocumentAttachmentId(attachmentId),
        )
    }

    class GetDocumentAttachmentResponse(
        data: DocumentAttachment
    ) : DefaultApiCompletedResponse<DocumentAttachment>(data)
}
