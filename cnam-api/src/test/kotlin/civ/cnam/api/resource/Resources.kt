package civ.cnam.api.resource

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.alert.MyAlertResource
import civ.cnam.api.resource.enrolment.CorrectiveActionResource
import civ.cnam.api.resource.enrolment.EnrolmentActionResource
import civ.cnam.api.resource.enrolment.EnrolmentResource
import civ.cnam.api.resource.enrolment.FingerprintsImageResource
import civ.cnam.api.resource.enrolment.VerificationTaskResource
import civ.cnam.api.resource.user.AccessTokenResource
import civ.cnam.api.resource.user.CnamUserResource
import civ.cnam.api.resource.user.OneTimePasswordChallengeResource
import civ.cnam.api.resource.user.PartnerUserResource
import civ.cnam.api.resource.user.PermissionResource
import civ.cnam.api.resource.user.PublicUserResource
import civ.cnam.api.resource.user.RoleResource
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource

object Resources {
    val accessToken: AccessTokenResource get() = unauthenticatedResource(ACCESS_TOKEN_RESOURCE, ::AccessTokenResource)
    val oneTimePasswordChallenge: OneTimePasswordChallengeResource
        get() =
        unauthenticatedResource(ONE_TIME_PASSWORD_CHALLENGE_RESOURCE, ::OneTimePasswordChallengeResource)
    val permission: PermissionResource get() = authenticatedResource(PERMISSION_RESOURCE, ::PermissionResource)
    val role: RoleResource get() = authenticatedResource(ROLE_RESOURCE, ::RoleResource)
    val cnamUser: CnamUserResource get() = authenticatedResource(CNAM_USER_RESOURCE, ::CnamUserResource)
    val partnerUser: PartnerUserResource get() = authenticatedResource(PARTNER_USER_RESOURCE, ::PartnerUserResource)
    val publicUser: PublicUserResource get() = unauthenticatedResource(PUBLIC_USER_RESOURCE, ::PublicUserResource)
    val enrolment: EnrolmentResource get() = authenticatedResource(ENROLMENT_RESOURCE, ::EnrolmentResource)
    val myAlert: MyAlertResource get() = authenticatedResource(MY_ALERT_RESOURCE, ::MyAlertResource)
    val verification: VerificationTaskResource get() =
        authenticatedResource(VERIFICATION_TASK_RESOURCE, ::VerificationTaskResource)
    val correctiveAction: CorrectiveActionResource get() =
        authenticatedResource(CORRECTIVE_ACTION_RESOURCE, ::CorrectiveActionResource)
    val enrolmentAction: EnrolmentActionResource get() =
        authenticatedResource(ENROLMENT_ACTION_RESOURCE, ::EnrolmentActionResource)
    val fingerprintImages: FingerprintsImageResource get() =
        authenticatedResource(FINGERPRINTS_IMAGE_RESOURCE, ::FingerprintsImageResource)

    private fun <T> unauthenticatedResource(name: String, constructResource: () -> T): T {
        val resource: T? = ctx[name]
        return when {
            resource == null -> constructResource().also { ctx[name] = it }
            else -> resource
        }
    }

    private fun <T : AbstractAuthenticatedResource> authenticatedResource(
        name: String,
        constructResource: (String) -> T
    ): T {
        val accessToken = userScenarioContext.accessToken
            ?: throw IllegalStateException("user access token is null")

        val resource: T? = ctx[name]
        return if (resource?.accessToken == accessToken) {
            resource
        } else {
            constructResource(accessToken).also { ctx[name] = it }
        }
    }

    private val ctx = ScenarioContext
    private val userScenarioContext = UserScenarioContext

    private const val ACCESS_TOKEN_RESOURCE = "ACCESS_TOKEN_RESOURCE"
    private const val ONE_TIME_PASSWORD_CHALLENGE_RESOURCE = "ONE_TIME_PASSWORD_CHALLENGE_RESOURCE"
    private const val PERMISSION_RESOURCE = "PERMISSION_RESOURCE"
    private const val ROLE_RESOURCE = "ROLE_RESOURCE"
    private const val CNAM_USER_RESOURCE = "CNAM_USER_RESOURCE"
    private const val PARTNER_USER_RESOURCE = "PARTNER_USER_RESOURCE"
    private const val PUBLIC_USER_RESOURCE = "PUBLIC_USER_RESOURCE"
    private const val ENROLMENT_RESOURCE = "ENROLMENT_RESOURCE"
    private const val FINGERPRINTS_IMAGE_RESOURCE = "FINGERPRINTS_IMAGE_RESOURCE"
    private const val ALERT_RESOURCE = "ALERT_RESOURCE"
    private const val MY_ALERT_RESOURCE = "MY_ALERT_RESOURCE"
    private const val VERIFICATION_TASK_RESOURCE = "VERIFICATION_TASK_RESOURCE"
    private const val CORRECTIVE_ACTION_RESOURCE = "CORRECTIVE_ACTION_RESOURCE"
    private const val ENROLMENT_ACTION_RESOURCE = "ENROLMENT_ACTION_RESOURCE"
}
