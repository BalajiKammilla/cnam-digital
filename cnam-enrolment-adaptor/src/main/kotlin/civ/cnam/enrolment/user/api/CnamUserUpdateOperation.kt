package civ.cnam.enrolment.user.api

import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.RESOURCE_ID
import dev.dry.common.function.Either
import dev.dry.core.data.model.entity.User
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.core.security.auth.model.value.Password
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.USER_UPDATE_VALUE
import dev.dry.user.domain.command.UpdateUser
import dev.dry.user.domain.command.UpdateUser.UserUpdated
import dev.dry.user.domain.error.UserUpdateError
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Path("/management/back-office/user/{userId}")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class CnamUserUpdateOperation(private val updateUser: UpdateUser) {
    @PATCH
    @PermissionsAllowed(value = [USER_UPDATE_VALUE])
    @Operation(summary = "Update a back office user")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = CnamUserUpdateResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = CnamUserUpdateErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "user", operationName = "update")
    @Transactional
    operator fun invoke(
        @AuditEventPropertySource(property = RESOURCE_ID, name = "USER_ID")
        @PathParam("userId") userId: Long,
        @Valid @RequestBody payload: CnamUserUpdateRequest,
    ): Either<UserUpdateError, UserUpdated> {
        return updateUser(
            userAccountId = User.ID(userId),
            displayName = payload.displayName,
            active = payload.active,
            password = payload.password(),
            userRoleNames = payload.userRoleNames,
        )
    }

    class CnamUserUpdateRequest(
        val displayName: String?,
        val active: Boolean?,
        val password: String?,
        val userRoleNames: Set<String>?,
    ) {
        fun password(): Password? = if (password != null) Password(password) else null
    }

    class CnamUserUpdateResponse(data: UserUpdated) : DefaultApiCompletedResponse<UserUpdated>(data)
    class CnamUserUpdateErrorResponse(error: UserUpdateError) : DefaultApiErrorResponse<UserUpdateError>(error)
}
