package civ.cnam.api.steps

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.data.TestUser
import civ.cnam.api.data.TestUser.HasPermission
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.restassured.response.Response
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

open class GlobalSteps {
    protected val ctx = ScenarioContext
    protected val user = UserScenarioContext

    @Given("^I am authenticated as a ([a-zA-Z]+) user$")
    fun given_i_am_authenticated_as_a_realm_user(realmName: String) {
        val response = createAccessToken(realmName, null)
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body().jsonPath()
        val otpRequired = jsonPath.getBoolean("data.otpRequired")
        user.accessToken = if (otpRequired) {
            val otpChallengeId = jsonPath.getString("data.otpChallengeId")
            verifyOtpChallengeAndGetAccessToken(otpChallengeId, TestUser.FIXED_OTP)
        } else {
            jsonPath.getString("data.accessToken")
        }
        assertThat(user.accessToken).isNotNull()
    }

    // ([a-z]+:[a-z]+(,[a-z]+:[a-z]+)*)
    @Given("^I am authenticated as a ([a-zA-Z]+) user with the permissions: ([a-z:, ]+)$")
    fun given_i_am_authenticated_as_a_realm_user_and_have_the_permissions(
        realmName: String,
        permissions: String,
    ) {
        val permissionList = permissions
            .split(",")
            .map { it.trim() }
            .toSet()
            .map { HasPermission(it) }
        val response = createAccessToken(realmName, null, permissionList)
        assertThat(response.getStatusCode()).isEqualTo(200);
        val jsonPath = response.body().jsonPath()
        assertThat(jsonPath.getBoolean("data.otpRequired")).isFalse()
        user.accessToken = jsonPath.getString("data.accessToken")
        assertThat(user.accessToken).isNotNull()
    }

    @Then("^the request fails with status \"(400|404|401)\", code \"([^\"]*)\", and message \"([^\"]*)\"")
    fun then_the_request_fails_status_code_and_message(status: Int, code: String, message: String) {
        val response = ctx.lastResponse ?: Assertions.fail("create access token response is null")
        response.prettyPrint()
        assertThat(response.statusCode).isEqualTo(status)
        val jsonPath = response.body().jsonPath()
        assertThat(jsonPath.getString("error.code")).isEqualTo(code)
        assertThat(jsonPath.getString("error.message")).isEqualTo(message)
    }

    private fun createAccessToken(
        realmName: String? = null,
        userName: String? = null,
        permissionList: List<HasPermission> = emptyList(),
    ): Response {
        val testUser = TestUser.select(realmName?.lowercase(), userName, permissionList)
        return Resources.accessToken.create(userName = testUser.userName, password = testUser.password)
    }

    private fun respondToOtpChallenge(otpChallengeId: String, otp: String): Response {
        return Resources.oneTimePasswordChallenge.verify(otpChallengeId = otpChallengeId, otp = otp)
    }

    private fun verifyOtpChallengeAndGetAccessToken(otpChallengeId: String, otp: String): String {
        val response = respondToOtpChallenge(otpChallengeId = otpChallengeId, otp = otp)
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body().jsonPath()
        return jsonPath.getString("data.accessToken")
    }
}