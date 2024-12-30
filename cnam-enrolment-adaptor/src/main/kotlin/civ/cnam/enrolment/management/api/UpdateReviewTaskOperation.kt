package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_UPDATE_VALUE
import civ.cnam.enrolment.domain.command.UpdateReviewTask
import civ.cnam.enrolment.domain.command.UpdateReviewTask.DedupeReviewTaskUpdate
import civ.cnam.enrolment.domain.command.UpdateReviewTask.IdentityDocumentReviewTaskUpdate
import civ.cnam.enrolment.domain.command.UpdateReviewTask.ReviewTaskUpdate
import civ.cnam.enrolment.domain.command.UpdateReviewTask.ReviewTaskUpdateType
import civ.cnam.enrolment.domain.error.UpdateReviewTaskError
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeMatchAttributes
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.RESOURCE_ID
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.UserName
import dev.dry.core.jaxrs.response.DefaultApiCompletedNoContentResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags
import java.time.LocalDate

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment/review-task/{reviewTaskId}")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class UpdateReviewTaskOperation(private val updateReviewTask: UpdateReviewTask) {
    @PATCH
    @PermissionsAllowed(value = [ENROLMENT_UPDATE_VALUE])
    @Operation(
        summary = "Update a review task",
        description = "Updates the status of a review task (approve or reject)"
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = UpdateReviewTaskResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = UpdateReviewTaskErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "review-task", operationName = "update")
    @Valid
    operator fun invoke(
        @AuditEventPropertySource(property = RESOURCE_ID)
        @PathParam("reviewTaskId") reviewTaskId: Long,
        @RequestBody @Valid request: ReviewTaskUpdateRequest,
        @Context ctx: SecurityContext,
    ): Either<UpdateReviewTaskError, Unit> {
        val id = ReviewTask.ID(reviewTaskId)
        val userName = UserName(ctx.userPrincipal.name)
        return updateReviewTask(id, userName, request.toReviewTaskUpdate())
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes(value = [
        JsonSubTypes.Type(
            value = IdentityDocumentReviewTaskUpdateRequest::class,
            name = "IDENTITY_DOCUMENT_REVIEW_TASK_UPDATE"),
        JsonSubTypes.Type(value = DedupeReviewTaskUpdateRequest::class, name = "DEDUPE_REVIEW_TASK_UPDATE"),
    ])
    @Schema(
        oneOf = [
            IdentityDocumentReviewTaskUpdateRequest::class,
            DedupeReviewTaskUpdateRequest::class
        ],
        discriminatorProperty = "type",
        discriminatorMapping = [
            DiscriminatorMapping(
                value = "IDENTITY_DOCUMENT_REVIEW_TASK_UPDATE",
                schema = IdentityDocumentReviewTaskUpdateRequest::class),
            DiscriminatorMapping(
                value = "DEDUPE_REVIEW_TASK_UPDATE",
                schema = DedupeReviewTaskUpdateRequest::class)
        ]
    )
    sealed interface ReviewTaskUpdateRequest {
        val type: ReviewTaskUpdateType
        val status: ReviewTaskStatus
        @get:Size(max = 1000)
        val note: String?

        fun toReviewTaskUpdate(): ReviewTaskUpdate
    }
    class IdentityDocumentReviewTaskUpdateRequest(
        override val status: ReviewTaskStatus,
        @Size(max = 1000)
        override val note: String?,
    ) : ReviewTaskUpdateRequest, IdentityDocumentReviewTaskUpdate {
        override val type: ReviewTaskUpdateType = ReviewTaskUpdateType.IDENTITY_DOCUMENT_REVIEW_TASK_UPDATE

        override fun toReviewTaskUpdate(): ReviewTaskUpdate = this
    }
    class DedupeReviewTaskUpdateRequest(
        override val status: ReviewTaskStatus,
        @Size(max = 1000)
        override val note: String?,
        val dedupeAttributes: DedupeReviewTaskUpdateAttributesData?,
    ) : ReviewTaskUpdateRequest {
        override val type: ReviewTaskUpdateType = ReviewTaskUpdateType.DEDUPE_REVIEW_TASK_UPDATE

        override fun toReviewTaskUpdate(): ReviewTaskUpdate = DedupeReviewTaskUpdate(
            status = status,
            note = note,
            dedupeAttributes = dedupeAttributes?.toDedupeMatchAttributes()
        )
    }
    class DedupeReviewTaskUpdateAttributesData(
        val firstName: String,
        val lastName: String,
        val dateOfBirth: LocalDate,
        val identityDocumentTypeCode: String,
        val identityDocumentNumber: String,
    ) {
        fun toDedupeMatchAttributes(): DedupeMatchAttributes {
            return DedupeMatchAttributes(
                firstName = FirstName(firstName),
                lastName = LastName(lastName),
                dateOfBirth = dateOfBirth,
                identityDocumentTypeCode = DocumentTypeCode(identityDocumentTypeCode),
                identityDocumentNumber = DocumentNumber(identityDocumentNumber),
            )
        }
    }

    class UpdateReviewTaskResponse : DefaultApiCompletedNoContentResponse()
    class UpdateReviewTaskErrorResponse(
        error: UpdateReviewTaskError
    ) : DefaultApiErrorResponse<UpdateReviewTaskError>(error)
}
