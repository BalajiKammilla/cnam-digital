package civ.cnam.enrolment.user.api

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.core.data.pagination.Page
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.USER_LIST_VALUE
import dev.dry.user.domain.model.query.GetUserAccountList
import dev.dry.user.domain.model.type.UserAccountListItem
import dev.dry.user.domain.model.value.UserAccountStatus.ACTIVE
import dev.dry.user.domain.model.value.UserAccountStatus.INACTIVE
import dev.dry.user.domain.model.value.UserAccountStatus.REGISTRATION_OTP_VERIFICATION_PENDING
import dev.dry.user.domain.model.value.UserAccountStatus.REGISTRATION_OTP_VERIFIED
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

@Path("/management/back-office/user")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class CnamUserGetListOperation(private val getUserAccountList: GetUserAccountList) {
    @GET
    @PermissionsAllowed(value = [USER_LIST_VALUE])
    @Operation(
        summary = "Get back office user list",
        description = "Get a paginated list of back office users"
    )
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = CnamUserGetListResponse::class))
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
        @QueryParam("active") active: Boolean?,
        @Length(min = 3)
        @QueryParam("emailAddress") emailAddress: String?,
    ): Page<UserAccountListItem> {
        return getUserAccountList(
            realmNames = setOf(RealmNames.CNAM_VALUE),
            statuses = when (active) {
                true -> setOf(ACTIVE)
                false -> setOf(INACTIVE, REGISTRATION_OTP_VERIFICATION_PENDING, REGISTRATION_OTP_VERIFIED)
                else -> null
            },
            fromCreatedAt = from,
            toCreatedAt = to,
            emailAddress = emailAddress,
            pageNumber = pageNumber,
            pageSize = pageSize,
        )
    }

    class CnamUserGetListResponse(
        data: Page<UserAccountListItem>
    ) : DefaultApiCompletedResponse<Page<UserAccountListItem>>(data)
}
