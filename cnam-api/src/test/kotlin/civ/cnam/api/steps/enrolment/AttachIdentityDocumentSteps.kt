package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class AttachIdentityDocumentSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @When("^I attach an identity document of type ([a-zA-Z]+) with number ([a-zA-Z0-9]+)$")
    fun when_i_attach_an_identity_document_of_type_with_number(
        documentTypeCode: String,
        documentNumber: String
    ) {
        ctx.lastResponse = Resources.enrolment.attachIdentityDocument(
            documentTypeCode = documentTypeCode,
            documentNumber = documentNumber,
        )
    }

    @Then("the identity document is successfully attached")
    fun then_the_identity_document_is_successfully_attached() {
        val response = ctx.lastResponse ?: fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        enrolment.enrolmentId = jsonPath.getString("data.enrolmentId")
        assertThat(enrolment.enrolmentId).isNotNull
        val documentAttachmentId = jsonPath.getString("data.documentAttachmentId")
        assertThat(documentAttachmentId).isNotNull
        // TODO("retrieve the document")
    }
}
