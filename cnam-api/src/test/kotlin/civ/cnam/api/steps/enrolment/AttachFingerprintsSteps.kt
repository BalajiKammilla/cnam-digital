package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class AttachFingerprintsSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @When("I attach my fingerprints")
    fun when_i_attach_my_fingerprints() {
        ctx.lastResponse = Resources.enrolment.attachFingerprints()
    }

    @Then("the fingerprints are successfully attached")
    fun then_the_fingerprints_are_successfully_attached() {
        val response = ctx.lastResponse ?: fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        val enrolmentId = jsonPath.getString("data.enrolmentId")
        assertThat(enrolmentId).isEqualTo(enrolment.enrolmentId)
        enrolment.fingerprintAttachmentId = jsonPath.getString("data.fingerprintsAttachmentId")
        assertThat(enrolment.fingerprintAttachmentId).isNotNull
    }
}
