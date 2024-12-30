package civ.cnam.api.steps

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class CorrectiveActionSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @When("I view corrective actions for this enrolment")
    fun when_i_view_corrective_actions_for_this_enrolment() {
        ctx.lastResponse = Resources.correctiveAction.getList(
            enrolmentId = enrolment.enrolmentId ?: fail("unable to check corrective actions enrolmentId is null"),
            status = null,
        )
    }

    @Then("^I find a corrective action of type ([A-Z_]+) with status ([A-Z_]+)$")
    fun then_i_find_a_corrective_action_of_type_with_status(
        correctiveActionType: String,
        correctiveActionStatus: String,
    ) {
        val response = ctx.lastResponse ?: fail("lastResponse for retrieving corrective actions is null")
        val type = CorrectiveActionType.valueOf(correctiveActionType)
        val status = CorrectiveActionStatus.valueOf(correctiveActionStatus)

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(200)

        val jsonPath = response.body.jsonPath()
        val correctiveActionList: List<Map<String, Any>> = jsonPath.getList("data.list")

        assertThat(correctiveActionList.find { it["type"] == type.name && it["status"] == status.name }).isNotNull
    }
}
