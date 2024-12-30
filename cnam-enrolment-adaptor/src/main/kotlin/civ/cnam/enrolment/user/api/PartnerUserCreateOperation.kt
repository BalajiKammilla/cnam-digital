package civ.cnam.enrolment.user.api

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.audit.domain.annotation.AuditEventPropertySource
import dev.dry.audit.domain.annotation.AuditedOperation
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.CUSTOM_1
import dev.dry.audit.domain.model.value.AuditEventPropertyKind.CUSTOM_2
import dev.dry.common.function.Either
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.core.data.validation.Mobile
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserBuildProperty.DRY_USER_MANAGEMENT_API_ENABLED
import dev.dry.user.constants.UserPermissionValue.PARTNER_CREATE_VALUE
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

@Path("/management/front-office/user")
@Tags(Tag(name = "User"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_USER_MANAGEMENT_API_ENABLED, stringValue = "true")
])
class PartnerUserCreateOperation(private val createUserAccount: CreateUserAccount) {
    @POST
    @PermissionsAllowed(value = [PARTNER_CREATE_VALUE])
    @Operation(summary = "Create a front-office assisted enrolment user")
    @APIResponse(responseCode = "200", content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PartnerUserCreateResponse::class))
    ])
    @APIResponse(content = [
        Content(mediaType = APPLICATION_JSON, schema = Schema(implementation = PartnerUserCreateErrorResponse::class))
    ])
    @Produces(APPLICATION_JSON)
    @AuditedOperation(resourceName = "user", operationName = "create")
    operator fun invoke(
        @AuditEventPropertySource(property = CUSTOM_1, path = "mobileNumber", name = "MOBILE_NUMBER")
        @AuditEventPropertySource(property = CUSTOM_2, path = "organisationNumber", name = "ORGANISATION_NUMBER")
        @Valid @RequestBody request: PartnerUserCreateRequest,
    ): Either<UserCreateError, CreateUserEvent> {
        return createUserAccount(
            NewUserAccountData(
                realmName = RealmNames.PARTNER,
                userName = request.mobileNumber,
                displayName = request.displayName,
                countryCode = IsoCountryCode.CI,
                password = request.password,
                mobileNumber = request.mobileNumber,
                emailAddress = null,
                alertChannel = UserAlertChannel.SMS,
                organisationNumber = request.organisationNumber,
                userRoleNames = emptySet(),
            )
        )
    }

    class PartnerUserCreateRequest(
        @get:Mobile
        val mobileNumber: String,
        val displayName: String,
        val password: String,
        val organisationNumber: String,
    )

    class PartnerUserCreateResponse(data: CreateUserEvent) : DefaultApiCompletedResponse<CreateUserEvent>(data)
    class PartnerUserCreateErrorResponse(error: UserCreateError) : DefaultApiErrorResponse<UserCreateError>(error)
}
