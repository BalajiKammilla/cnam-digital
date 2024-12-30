package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class GetRoleListSteps {
    private var ctx = ScenarioContext

    @When("I request the role list")
    fun when_i_request_the_role_list() {
        ctx.lastResponse = Resources.role.getList()
    }

    @Then("the role list is retrieved")
    fun then_the_role_list_is_retrieved() {
        val response = ctx.lastResponse ?: fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(response.body().jsonPath().getList<String>("data.name")).isNotEmpty
    }
}