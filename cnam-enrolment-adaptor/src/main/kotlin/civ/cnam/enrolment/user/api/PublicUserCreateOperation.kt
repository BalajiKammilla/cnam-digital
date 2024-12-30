package civ.cnam.enrolment.user.api

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.CUSTOM_1
import dev.dry.common.function.Either
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.core.data.validation.Mobile
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.domain.command.CreateUserAccount
import dev.dry.user.domain.command.CreateUserAccount.CreateUserEvent
import dev.dry.user.domain.error.UserCreateError
import dev.dry.user.domain.model.type.NewUserAccountData
import dev.dry.user.domain.model.value.UserAlertChannel
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.PermitAll
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
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

@Path("/user/public")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class PublicUserCreateOperation(private val createUserAccount: CreateUserAccount) {
    @POST
    @PermitAll
    @Operation(summary = "Create a public user")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PublicUserCreateResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PublicUserCreateErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "user", operationName = "create")
    operator fun invoke(
        @AuditEventPropertySource(property = CUSTOM_1, path = "mobileNumber", name = "MOBILE_NUMBER")
        @Valid @RequestBody request: PublicUserCreateRequest,
    ): Either<UserCreateError, CreateUserEvent> {
        return createUserAccount(
            NewUserAccountData(
                realmName = RealmNames.PUBLIC,
                userName = request.mobileNumber,
                displayName = request.displayName,
                countryCode = IsoCountryCode.CI,
                password = request.password,
                mobileNumber = request.mobileNumber,
                emailAddress = null,
                alertChannel = UserAlertChannel.SMS,
                organisationNumber = null,
                userRoleNames = emptySet(),
            )
        )
    }

    class PublicUserCreateRequest(
        @get:Mobile
        val mobileNumber: String,
        @Size(min = 3, max = 200)
        val displayName: String,
        val password: String,
    )

    class PublicUserCreateResponse(data: CreateUserEvent) : DefaultApiCompletedResponse<CreateUserEvent>(data)
    class PublicUserCreateErrorResponse(error: UserCreateError) : DefaultApiErrorResponse<UserCreateError>(error)
}
