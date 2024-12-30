package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import dev.dry.assertj.hasStringEntrySatisfying
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class VerifyOneTimePasswordSteps {
    private val ctx = ScenarioContext
    private val user = UserScenarioContext

    @When("I submit the correct OTP to a generated challenge")
    fun when_i_submit_the_correct_OTP_to_the_generated_challenge() {
        ctx.lastResponse = Resources.oneTimePasswordChallenge.verify(
            otpChallengeId = user.otpChallengeId ?: fail("otpChallengeId is null"),
            otp = FIXED_OTP,
        )
    }

    @Then("^the OTP is verified with the kind ([A-Z_]+)$")
    fun then_the_OTP_is_verified_with_the_kind(kind: String) {
        val response = ctx.lastResponse ?: fail("verifyOtpResponse is null")

        assertThat(response.statusCode).isEqualTo(200)

        val jsonPath = response.body().jsonPath()
        assertThat(jsonPath.getMap<String, Any>("data"))
            .`as`("Verify OTP Response Body")
            .isNotNull
            .hasStringEntrySatisfying("kind") { assertThat(it).isEqualTo(kind) }
    }

    companion object {
        const val FIXED_OTP = "1111"
    }
}
