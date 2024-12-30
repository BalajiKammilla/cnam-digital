package civ.cnam.enrolment.reporting.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_REPORTING_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.DASHBOARD_READ_VALUE
import civ.cnam.enrolment.domain.model.query.enrolment.GetDashboardMetrics
import civ.cnam.enrolment.domain.model.type.enrolment.reporting.DashboardMetrics
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
@Tags(Tag(name = "Reporting"))
@Path("/reporting/dashboard/metrics")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_REPORTING_API_ENABLED, stringValue = "true")
])
class GetDashboardMetricsOperation(private val getDashboardMetrics: GetDashboardMetrics) {
    @GET
    @PermissionsAllowed(value = [DASHBOARD_READ_VALUE])
    @Operation(summary = "Get enrolment metrics")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = GetEnrolmentMetricsResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @QueryParam("fromDate") fromDate: LocalDate?,
        @QueryParam("toDate") toDate: LocalDate?,
    ): GetEnrolmentMetricsResponse {
        val metrics = getDashboardMetrics(fromDate = fromDate, toDate = toDate)
        return GetEnrolmentMetricsResponse(metrics)
    }

    class GetEnrolmentMetricsResponse(val metrics: DashboardMetrics)
}
