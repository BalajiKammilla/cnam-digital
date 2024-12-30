package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.repository.referencedata.CompanyRepository
import dev.dry.core.data.pagination.Page
import dev.dry.core.data.pagination.PageRequest
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/reference-data/company")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class GetCompaniesFilteredByLabelOperation(private val companyRepository: CompanyRepository) {
    @Valid
    @GET
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(summary = "Get companies filtered by a partial name match")
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = Schema(implementation = GetAgenciesFilteredBySubPrefectureResponse::class)
        )
    ])
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(
        @Min(1)
        @QueryParam("pageNumber")
        pageNumber: Int,
        @Max(1000)
        @QueryParam("pageSize")
        pageSize: Int,
        @QueryParam("labelContains")
        labelContains: String,
    ): CompaniesSearchResult {
        val searchResults = companyRepository.findByLabelLike(labelContains, PageRequest(pageNumber, pageSize))
        return CompaniesSearchResult(searchResults)
    }

    class CompaniesSearchResult(val searchResults: Page<Company>)

    class GetAgenciesFilteredBySubPrefectureResponse(
        data: CompaniesSearchResult
    ) : DefaultApiCompletedResponse<CompaniesSearchResult>(data)
}
