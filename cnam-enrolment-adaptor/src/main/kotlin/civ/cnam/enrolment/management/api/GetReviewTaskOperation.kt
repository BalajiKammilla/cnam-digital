package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_READ_VALUE
import civ.cnam.enrolment.domain.error.GetReviewTaskDetailsError
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.query.enrolment.GetReviewTaskDetails
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeReviewTaskData
import civ.cnam.enrolment.domain.model.type.enrolment.IdentityDocumentReviewTaskData
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskData
import dev.dry.common.function.Either
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment/review-task/{reviewTaskId}")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetReviewTaskOperation(private val getReviewTaskDetails: GetReviewTaskDetails) {
    @GET
    @PermissionsAllowed(value = [ENROLMENT_READ_VALUE])
    @Operation(
        summary = "Get review task",
        description = "Retrieves review task details"
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetReviewTaskResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(@PathParam("reviewTaskId") reviewTaskId: Long): Either<GetReviewTaskDetailsError, ReviewTaskData> {
        return getReviewTaskDetails(ReviewTask.ID(reviewTaskId))
    }

    @Schema(
        oneOf = [
            GetIdentityDocumentReviewTaskResponse::class,
            GetDedupeReviewTaskResponse::class
        ],
        discriminatorProperty = "data.type",
        discriminatorMapping = [
            DiscriminatorMapping(
                value = "IDENTITY_DOCUMENT",
                schema = GetIdentityDocumentReviewTaskResponse::class),
            DiscriminatorMapping(
                value = "DEDUPE",
                schema = GetDedupeReviewTaskResponse::class)
        ]
    )
    sealed class GetReviewTaskResponse<T : ReviewTaskData>(
        data: T
    ) : DefaultApiCompletedResponse<T>(data)

    class GetIdentityDocumentReviewTaskResponse(
        data: IdentityDocumentReviewTaskData
    ) : GetReviewTaskResponse<IdentityDocumentReviewTaskData>(data)

    class GetDedupeReviewTaskResponse(
        data: DedupeReviewTaskData
    ) : GetReviewTaskResponse<DedupeReviewTaskData>(data)
}
