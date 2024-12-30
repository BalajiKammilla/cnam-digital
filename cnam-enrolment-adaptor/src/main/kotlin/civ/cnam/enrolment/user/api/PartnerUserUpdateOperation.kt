package civ.cnam.enrolment.user.api

import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.RESOURCE_ID
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.data.model.entity.User
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.PARTNER_UPDATE_VALUE
import dev.dry.user.domain.command.UpdateUser
import dev.dry.user.domain.error.UserUpdateError
import dev.dry.user.domain.model.type.UserAccountData
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Path("/management/front-office/user/{userId}")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class PartnerUserUpdateOperation(private val updateUser: UpdateUser) {
    @PATCH
    @PermissionsAllowed(value = [PARTNER_UPDATE_VALUE])
    @Operation(summary = "Update a front-office enrolment user")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PartnerUserUpdateResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PartnerUserUpdateErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "user", operationName = "update")
    operator fun invoke(
        @AuditEventPropertySource(property = RESOURCE_ID, name = "USER_ID")
        @PathParam("userId") userId: User.ID,
        @Valid @RequestBody request: PartnerUserUpdateRequest,
    ): Either<UserUpdateError, UserAccountData> {
        return updateUser(
            userAccountId = userId,
            active = request.active,
            displayName = request.displayName,
            password = null,
        ).map { UserAccountData(it.userAccount) }
    }

    class PartnerUserUpdateRequest(
        val active: Boolean?,
        val displayName: String?,
    )

    class PartnerUserUpdateResponse(data: UserAccountData) : DefaultApiCompletedResponse<UserAccountData>(data)
    class PartnerUserUpdateErrorResponse(error: UserUpdateError) : DefaultApiErrorResponse<UserUpdateError>(error)
}
