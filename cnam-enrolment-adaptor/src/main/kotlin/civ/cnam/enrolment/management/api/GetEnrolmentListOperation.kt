package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_LIST_VALUE
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.query.enrolment.FilterEnrolments
import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter.DateRangeProperty
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.data.pagination.Page
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
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
import java.time.LocalDate

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetEnrolmentListOperation(private val filterEnrolments: FilterEnrolments) {
    @GET
    @PermissionsAllowed(value = [ENROLMENT_LIST_VALUE])
    @Operation(summary = "Get a paginated list of enrolments filtered by the specified criteria")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = GetEnrolmentListResponse::class))
    ])
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(
        @QueryParam("pageNumber") pageNumber: Int,
        @QueryParam("pageSize") pageSize: Int,
        @QueryParam("dateRangeProperty") dateRangeProperty: DateRangeProperty?,
        @QueryParam("fromDate") fromDate: LocalDate?,
        @QueryParam("toDate") toDate: LocalDate?,
        @QueryParam("gender") gender: GenderCode?,
        @QueryParam("location") location: SubPrefectureCode?,
        @QueryParam("fromAge") fromAge: Int?,
        @QueryParam("toAge") toAge: Int?,
        @QueryParam("nationality") nationality: IsoAlpha3CountryCode?,
        @QueryParam("personCategory") personCategory: PersonCategoryCode?,
        @QueryParam("completed") completed: Boolean?,
        @QueryParam("approvalRequired") approvalRequired: Boolean?,
        @QueryParam("approvalStatus") approvalStatus: ApprovalStatus?,
        @QueryParam("processed") processed: Boolean?,
        @QueryParam("firstNames") firstNames: String?,
        @QueryParam("lastName") lastName: String?,
        @QueryParam("mobileNumber") mobileNumber: MobileNumber?,
        @QueryParam("emailAddress") emailAddress: EmailAddress?,
        @QueryParam("enrolmentId") enrolmentId: EnrolmentId?,
    ): FilterEnrolmentsResult {
        val filter = EnrolmentFilter(
            dateRangeProperty = dateRangeProperty,
            fromDate = fromDate,
            toDate = toDate,
            gender = gender,
            location = location,
            fromAge = fromAge,
            toAge = toAge,
            nationality = nationality,
            personCategory = personCategory,
            completed = completed,
            approvalRequired = approvalRequired,
            approvalStatus = approvalStatus,
            processed = processed,
            firstNames = firstNames,
            lastName = lastName,
            mobileNumber = mobileNumber,
            emailAddress = emailAddress,
            enrolmentId = enrolmentId,
        )
        val results = filterEnrolments(pageNumber = pageNumber, pageSize = pageSize, filter = filter)
        return FilterEnrolmentsResult(results)
    }

    class FilterEnrolmentsResult(val results: Page<EnrolmentListItem>)

    class GetEnrolmentListResponse(
        data: FilterEnrolmentsResult
    ) : DefaultApiCompletedResponse<FilterEnrolmentsResult>(data)
}
