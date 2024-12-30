package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class CompleteEnrolmentSteps {
    val ctx = ScenarioContext
    val enrolment = EnrolmentContext

    @When("I complete the enrolment")
    fun when_i_complete_the_enrolment() {
        ctx.lastResponse = Resources.enrolment.completeEnrolment()
    }

    @Then("the enrolment is successfully completed")
    fun then_the_enrolment_is_successfully_completed() {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")
        print(response.prettyPrint())
        assertThat(response.statusCode).isEqualTo(200)
    }
}
