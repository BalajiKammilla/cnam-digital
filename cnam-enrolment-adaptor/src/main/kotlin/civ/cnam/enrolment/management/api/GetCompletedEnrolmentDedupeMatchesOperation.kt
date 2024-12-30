package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.DEDUPE_LIST_VALUE
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
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
@Tags(Tag(name = "Enrolment Management"))
//@Path("/management/enrolment/{enrolmentId}/dedupe-matches")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetCompletedEnrolmentDedupeMatchesOperation(private val enrolmentRepository: EnrolmentRepository) {
    @GET
    @PermissionsAllowed(value = [DEDUPE_LIST_VALUE])
    @Operation(summary = "Get a list of dedupe matches for the specified enrolment")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetCompletedEnrolmentDedupeMatchesResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetCompletedEnrolmentDedupeMatchesErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @PathParam("enrolmentId") enrolmentId: EnrolmentId,
    ): Either<CodedError, CompletedEnrolmentDedupeMatchesResponsePayload> {
        return enrolmentRepository.findDedupeMatches(enrolmentId)
            .map(GetCompletedEnrolmentDedupeMatchesOperation::CompletedEnrolmentDedupeMatchesResponsePayload)
    }

    class CompletedEnrolmentDedupeMatchesResponsePayload(val matches: List<CompletedEnrolment>)

    class GetCompletedEnrolmentDedupeMatchesResponse(
        data: CompletedEnrolmentDedupeMatchesResponsePayload
    ) : DefaultApiCompletedResponse<CompletedEnrolmentDedupeMatchesResponsePayload>(data)
    class GetCompletedEnrolmentDedupeMatchesErrorResponse(error: CodedError) : DefaultApiErrorResponse<CodedError>(error)
}
