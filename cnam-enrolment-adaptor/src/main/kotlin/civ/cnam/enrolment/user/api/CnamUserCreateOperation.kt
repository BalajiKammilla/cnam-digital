package civ.cnam.enrolment.user.api

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.CUSTOM_1
import dev.dry.common.function.Either
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.USER_CREATE_VALUE
import dev.dry.user.domain.command.CreateUserAccount
import dev.dry.user.domain.command.CreateUserAccount.CreateUserEvent
import dev.dry.user.domain.error.UserCreateError
import dev.dry.user.domain.model.type.NewUserAccountData
import dev.dry.user.domain.model.value.UserAlertChannel
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.security.PermissionsAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Path("/management/back-office/user")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class CnamUserCreateOperation(private val createUserAccount: CreateUserAccount) {
    @POST
    @PermissionsAllowed(value = [USER_CREATE_VALUE])
    @Operation(summary = "Create a CNAM user")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = CnamUserCreateResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = CnamUserCreateErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "user", operationName = "create")
    operator fun invoke(
        @AuditEventPropertySource(property = CUSTOM_1, path = "emailAddress", name = "EMAIL_ADDRESS")
        @Valid @RequestBody request: CnamUserCreateRequest,
    ): Either<UserCreateError, CreateUserEvent> {
        return createUserAccount(
            NewUserAccountData(
                realmName = RealmNames.CNAM,
                userName = request.emailAddress,
                displayName = request.displayName,
                countryCode = IsoCountryCode.CI,
                password = request.password,
                mobileNumber = null,
                emailAddress = request.emailAddress,
                alertChannel = UserAlertChannel.EMAIL,
                organisationNumber = null,
                userRoleNames = request.userRoles,
            )
        )
    }

    class CnamUserCreateRequest(
        val emailAddress: String,
        val displayName: String,
        val password: String,
        @Schema(title = "names of user roles to assign to the back office user")
        val userRoles: Set<String>
    )

    class CnamUserCreateResponse(data: CreateUserEvent) : DefaultApiCompletedResponse<CreateUserEvent>(data)
    class CnamUserCreateErrorResponse(error: UserCreateError) : DefaultApiErrorResponse<UserCreateError>(error)
}
