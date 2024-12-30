package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class EnrolmentActionSteps {
    val enrolment = EnrolmentContext
    val user = UserScenarioContext
    private var enrolResponse: Response? = null

    @Then("^the ([A-Z_]+) action is added to the enrolment$")
    fun the_action_is_added_to_the_enrolment(actionName: String) {
        enrolResponse = Resources.enrolmentAction.getList(
            pageNumber = 1,
            pageSize = 10,
            enrolmentId = enrolment.enrolmentId
        )

        val response = enrolResponse ?: fail("enrolResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val actualActions = response.jsonPath().getList<String>("data.page.content.kind")
        assertThat(actualActions).isNotNull
        assertThat(actualActions).contains(actionName)
    }

    @Then("the enrolment has {int} actions")
    fun the_enrolment_has_expected_number_of_actions(expectedNumberOfActions: Int) {
        val actualNumberOfActions = enrolResponse?.jsonPath()?.getList<String>("data.page.content.kind")?.size
        assertThat(actualNumberOfActions).isNotNull
        assertThat(actualNumberOfActions).isEqualTo(expectedNumberOfActions)
    }
}