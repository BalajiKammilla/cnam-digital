package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.data.UserBuilder
import civ.cnam.api.resource.Resources
import dev.dry.assertj.hasIntEntrySatisfying
import dev.dry.assertj.hasStringEntrySatisfying
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class CreatePublicUserSteps {
    private val ctx = ScenarioContext
    private val user = UserScenarioContext

    @When("I submit a request to create a public user")
    fun when_i_request_creation_of_a_new_public_user() {
        val requestPayload = UserBuilder.publicUserCreateRequest()
        user.userName = requestPayload.mobileNumber
        user.password = requestPayload.password
        ctx.lastResponse = Resources.publicUser.create(requestPayload)
    }

    @Then("the user is created and an OTP challenge is generated")
    fun then_the_user_is_created_and_an_OTP_challenge_generated() {
        val response = ctx.lastResponse ?: fail("createResponse is null")

        assertThat(response.statusCode).isEqualTo(200)

        val jsonPath = response.body().jsonPath()

        assertThat(jsonPath.getMap<String, Any>("data"))
            .`as`("Create Public User Response Body")
            .isNotNull
            .hasStringEntrySatisfying("otpChallengeId") {
                assertThat(it)
                    .isNotNull
                    .hasSize(36)
                user.otpChallengeId = it
            }
            .hasStringEntrySatisfying("otpGeneratedAt") { assertThat(it).isNotNull }
            .hasIntEntrySatisfying("otpValidityDurationInMinutes") { assertThat(it).isEqualTo(5) }
    }
}
