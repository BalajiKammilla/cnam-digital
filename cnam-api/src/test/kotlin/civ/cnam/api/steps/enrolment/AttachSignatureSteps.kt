package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class AttachSignatureSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @When("I attach my signature")
    fun when_i_attach_my_signature() {
        ctx.lastResponse = Resources.enrolment.attachSignature()
    }

    @Then("the signature is successfully attached")
    fun then_the_signature_is_successfully_attached() {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        val enrolmentId = jsonPath.getString("data.enrolmentId")
        assertThat(enrolmentId).isEqualTo(enrolment.enrolmentId)
        val signatureAttachmentId = jsonPath.getString("data.signatureAttachmentId")
        assertThat(signatureAttachmentId).isNotNull
    }
}
