package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.repository.referencedata.AgencyRepository
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Path("/enrolment/reference-data/agency")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetAgenciesFilteredBySubPrefectureOperation(private val agencyRepository: AgencyRepository) {
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Get agencies filtered by sub-prefecture")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = GetAgenciesFilteredBySubPrefectureResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @QueryParam("subPrefectureCode") subPrefectureCode: SubPrefectureCode,
    ): AgenciesFilteredBySubPrefectureResult {
        val agencies = agencyRepository.findBySubPrefectureCode(subPrefectureCode)
        return AgenciesFilteredBySubPrefectureResult(agencies)
    }

    class AgenciesFilteredBySubPrefectureResult(val agencies: List<Agency>)

    class GetAgenciesFilteredBySubPrefectureResponse(
        data: AgenciesFilteredBySubPrefectureResult
    ) : DefaultApiCompletedResponse<AgenciesFilteredBySubPrefectureResult>(data)
}
