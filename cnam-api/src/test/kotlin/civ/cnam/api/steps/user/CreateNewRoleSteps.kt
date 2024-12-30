package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.constants.EnrolmentPermissionValue
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.fail

class CreateNewRoleSteps {
    private var ctx = ScenarioContext

    @When("I request to create the new role")
    fun I_request_to_create_the_new_role() {
        ctx.lastResponse = Resources.role.create("java","java developer","enrolment:read","enrolment:update")
    }

    @Then("the new role is created")
    fun the_new_role_is_created() {
        val response = ctx.lastResponse ?: fail("lastResponse is null")
        Assertions.assertThat(response).isNotNull
        Assertions.assertThat(response.statusCode).isEqualTo(200)
    }
}