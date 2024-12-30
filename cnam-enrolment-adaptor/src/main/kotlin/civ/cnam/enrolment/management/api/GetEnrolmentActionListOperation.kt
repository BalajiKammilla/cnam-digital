package civ.cnam.enrolment.management.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_MANAGEMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentPermissionValue.ENROLMENT_LIST_VALUE
import civ.cnam.enrolment.domain.model.query.enrolment.FilterEnrolmentActions
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionListItem
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import civ.cnam.enrolment.domain.model.value.EnrolmentId
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
import java.time.LocalDate
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Tags(Tag(name = "Enrolment Management"))
@Path("/management/enrolment/action")
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class GetEnrolmentActionListOperation(private val filterEnrolmentActions: FilterEnrolmentActions) {
    @GET
    @PermissionsAllowed(value = [ENROLMENT_LIST_VALUE])
    @Operation(
        summary = "Get filtered paginated list of the EnrolmentAction entity",
        description = "Retrieves a paginated list of the EnrolmentAction entity by the specified criteria"
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = EnrolmentActionListResponse::class))
    ])
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(
        @QueryParam("pageNumber") pageNumber: Int,
        @QueryParam("pageSize") pageSize: Int,
        @QueryParam("fromCreatedAt") fromCreatedAt: LocalDate?,
        @QueryParam("toCreatedAt") toCreatedAt: LocalDate?,
        @QueryParam("kind") kind: EnrolmentActionKind?,
        @QueryParam("mobileNumber") mobileNumber: MobileNumber?,
        @QueryParam("enrolmentId") enrolmentId: EnrolmentId
    ):EnrolmentActionListData {
        val filter = EnrolmentActionFilter(
            fromCreatedAt = fromCreatedAt,
            toCreatedAt = toCreatedAt,
            kind = kind,
            mobileNumber = mobileNumber,
            enrolmentId = enrolmentId
        )
        val page = filterEnrolmentActions(pageNumber = pageNumber, pageSize = pageSize, filter = filter)
        return EnrolmentActionListData(page)
    }

    class EnrolmentActionListData(val page: Page<EnrolmentActionListItem>)

    class EnrolmentActionListResponse(
        data: EnrolmentActionListData
    ): DefaultApiCompletedResponse<EnrolmentActionListData>(data)
}