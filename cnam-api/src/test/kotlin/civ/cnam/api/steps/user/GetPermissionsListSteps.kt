package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class GetPermissionsListSteps {
    private var ctx = ScenarioContext

    @When("I request the permissions list")
    fun i_request_the_permissions_list() {
        ctx.lastResponse = Resources.permission.listByKeyword(null)
    }

    @Then("the permissions list is retrieved")
    fun the_permissions_list_is_retrieved() {
        val response = ctx.lastResponse ?: fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(response.body().jsonPath().getList<String>("data.permission.value")).isNotEmpty
    }
}
