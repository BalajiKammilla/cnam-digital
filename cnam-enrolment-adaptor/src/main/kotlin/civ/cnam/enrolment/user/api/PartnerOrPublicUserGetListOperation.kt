package civ.cnam.enrolment.user.api

import civ.cnam.enrolment.constants.RealmNames
import civ.cnam.enrolment.user.api.FrontOfficeUserType.ASSISTED_ENROLMENT_USER
import civ.cnam.enrolment.user.api.FrontOfficeUserType.SELF_ENROLMENT_USER
import dev.dry.core.data.pagination.Page
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.PARTNER_LIST_VALUE
import dev.dry.user.domain.model.query.GetUserAccountList
import dev.dry.user.domain.model.type.UserAccountListItem
import dev.dry.user.domain.model.value.UserAccountStatus
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
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
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

@Path("/management/front-office/user")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class PartnerOrPublicUserGetListOperation(private val getUserAccountList: GetUserAccountList) {
    @GET
    @PermissionsAllowed(value = [PARTNER_LIST_VALUE])
    @Operation(
        summary = "Get front office user list",
        description =
            "Retrieve a paginated, and optionally filtered, list of front office users." +
            " A **front office user** -- also referred to as an **enrolment user**," +
            " is either a **self-enrolment user** that can self-enrols themselves and family members or" +
            " an **assisted-enrolment user** that performs assisted enrolments."
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PartnerOrPublicUserGetListResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @Min(value = 1)
        @QueryParam("pageNumber") pageNumber: Int,
        @Min(value = 1)
        @Max(value = 1000)
        @QueryParam("pageSize") pageSize: Int,
        @QueryParam("from") from: LocalDate?,
        @QueryParam("to") to: LocalDate?,
        @QueryParam("status") status: UserAccountStatus?,
        @QueryParam("type") type: FrontOfficeUserType?,
        @Length(min = 3)
        @QueryParam("mobileNumber") mobileNumber: String?
    ): Page<UserAccountListItem> {
        return getUserAccountList(
            realmNames = when (type) {
                SELF_ENROLMENT_USER -> setOf(RealmNames.PUBLIC_VALUE)
                ASSISTED_ENROLMENT_USER -> setOf(RealmNames.PARTNER_VALUE)
                null -> null
            },
            statuses = status?.let { setOf(it) },
            fromCreatedAt = from,
            toCreatedAt = to,
            mobileNumber = mobileNumber,
            pageNumber = pageNumber,
            pageSize = pageSize,
        )
    }

    class PartnerOrPublicUserGetListResponse(
        data: Page<UserAccountListItem>
    ) : DefaultApiCompletedResponse<Page<UserAccountListItem>>(data)
}
