package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_READ_VALUE
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment/{enrolmentId}")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetPartialEnrolmentOperation(private val enrolmentRepository: EnrolmentRepository) {
    class PartialEnrolmentResponsePayload(val enrolment: PartialEnrolment)

    @GET
    @PermissionsAllowed(value = [ENROLMENT_READ_VALUE])
    @Operation(
        summary = "Get partial details of an enrolment",
        description = "Partial details may be retrieved for a partial (incomplete) or completed enrolment"
    )
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = GetPartialEnrolmentResponse::class)
        )
    ])
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(
        @PathParam("enrolmentId") enrolmentId: EnrolmentId,
    ): Either<CodedError, PartialEnrolmentResponsePayload> {
        return enrolmentRepository.findPartialEnrolment(enrolmentId).map(GetPartialEnrolmentOperation::PartialEnrolmentResponsePayload)
    }

    class GetPartialEnrolmentResponse(
        data: GetEnrolmentListOperation.FilterEnrolmentsResult
    ) : DefaultApiCompletedResponse<GetEnrolmentListOperation.FilterEnrolmentsResult>(data)
}
