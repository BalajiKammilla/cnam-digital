package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import dev.dry.assertj.hasBooleanEntrySatisfying
import dev.dry.assertj.hasStringEntrySatisfying
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class CreateAccessTokenSteps {
    private val ctx = ScenarioContext
    private val user = UserScenarioContext

    @When("^I request an access token using username \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun when_i_request_an_access_token_using_username_and_password(userName: String, password: String) {
        ctx.lastResponse = Resources.accessToken.create(userName = userName, password = password)
    }

    @When("I request an access token")
    fun when_i_request_an_access_token() {
        ctx.lastResponse = Resources.accessToken.create(
            userName = "+225" + (user.userName ?: Assertions.fail("UserScenarioContext.userName is null")),
            password = user.password ?: Assertions.fail("UserScenarioContext.password is null"),
        )
    }

    @Then("an access token is generated")
    fun then_an_access_token_is_generated() {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")

        assertThat(response.statusCode).isEqualTo(200)

        val jsonPath = response.body().jsonPath()
        assertThat(jsonPath.getMap<String, Any>("data"))
            .`as`("Create Access Token Response Body")
            .isNotNull
            .hasBooleanEntrySatisfying("otpRequired") { assertThat(it).isEqualTo(false) }
            .hasStringEntrySatisfying("accessToken") {
                assertThat(it).isNotNull
                user.accessToken = it
            }
    }
}
