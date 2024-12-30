package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_LIST_VALUE
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.query.enrolment.FilterReviewTasks
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskFilter
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskListItem
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.core.data.pagination.Page
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.inject.Singleton
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
import java.time.LocalDate

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment/review-task")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetReviewTaskListOperation(private val filterReviewTasks: FilterReviewTasks) {
    @GET
    @PermissionsAllowed(value = [ENROLMENT_LIST_VALUE])
    @Operation(
        summary = "Get list of review tasks",
        description = "Retrieves a paginated list of review tasks filtered by the specified criteria"
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetReviewTaskListResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @QueryParam("pageNumber") pageNumber: Int,
        @QueryParam("pageSize") pageSize: Int,
        @QueryParam("dateRangeProperty") dateRangeProperty: ReviewTaskFilter.DateRangeProperty?,
        @QueryParam("fromDate") fromDate: LocalDate?,
        @QueryParam("toDate") toDate: LocalDate?,
        @QueryParam("status") status: ReviewTask.ReviewTaskStatus?,
        @QueryParam("enrolmentId") enrolmentId: String?,
    ): GetReviewTaskListData {
        val filter = ReviewTaskFilter(
            dateRangeProperty = dateRangeProperty,
            fromDate = fromDate,
            toDate = toDate,
            status = status,
            enrolmentId = enrolmentId?.let { EnrolmentId(it) },
        )
        val page = filterReviewTasks(pageNumber = pageNumber, pageSize = pageSize, filter = filter)
        return GetReviewTaskListData(page)
    }

    class GetReviewTaskListData(val page: Page<ReviewTaskListItem>)

    class GetReviewTaskListResponse(
        data: GetReviewTaskListData
    ) : DefaultApiCompletedResponse<GetReviewTaskListData>(data)
}
