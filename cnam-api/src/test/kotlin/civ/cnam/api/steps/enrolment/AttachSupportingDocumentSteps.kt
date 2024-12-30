package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class AttachSupportingDocumentSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @When("^I attach a supporting document of type ([a-zA-Z]+) as ([A-Z_]+)$")
    fun when_i_attach_an_identity_document_of_type_with_number(
        documentTypeCode: String,
        purpose: String,
    ) {
        ctx.lastResponse = Resources.enrolment.attachSupportingDocument(
            documentTypeCode = documentTypeCode,
            purpose = SupportingDocument.Purpose.valueOf(purpose),
        )
    }

    @Then("the supporting document is successfully attached")
    fun then_the_supporting_document_is_successfully_attached() {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        val enrolmentId = jsonPath.getString("data.enrolmentId")
        assertThat(enrolmentId).isEqualTo(enrolment.enrolmentId)
        val documentAttachmentId = jsonPath.getString("data.documentAttachmentId")
        assertThat(documentAttachmentId).isNotNull
    }
}
