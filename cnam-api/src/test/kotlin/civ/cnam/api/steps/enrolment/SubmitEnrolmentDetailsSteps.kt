package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.data.EnrolmentDetailsBuilder
import civ.cnam.api.data.TestSupportingDocuments
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class SubmitEnrolmentDetailsSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext
    val enrolment = EnrolmentContext

    @DataTableType
    fun createRequestTransformer(row: Map<String, String>): EnrolmentDetailsData {
        val builder = EnrolmentDetailsBuilder()
        row["age"]?.also { builder.age(it.toLong()) }
        row["firstNames"]?.also { builder.firstNames(it) }
        row["lastName"]?.also { builder.lastName(it) }
        row["title"]?.also { builder.titleCode(it) }
        row["maritalStatus"]?.also { builder.maritalStatusCode(it) }
        row["personType"]?.also { builder.personTypeCode(it) }
        row["paidBy"]?.also { builder.paidByCode(it) }
        row["payerCnamNumber"]?.also { builder.payerCnamNumber(it) }
        row["payerEnrolmentId"]?.also { builder.payerEnrolmentId(it) }
        row["mobileNumber"]?.also { builder.mobileNumber(it) }
        return builder.build()
    }

    @When("I submit enrolment details from the following table")
    fun when_i_submit_enrolment_details_from_the_following_table(request: EnrolmentDetailsData) {
        ctx.lastResponse = Resources.enrolment.submitEnrolmentDetails(request)
    }

    @Then("enrolment details are successfully saved")
    fun then_enrolment_details_are_successfully_saved() {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        val enrolmentId = jsonPath.getString("data.enrolmentId")
        assertThat(enrolmentId).isEqualTo(enrolment.enrolmentId)
        val supportingDocumentPurposes = jsonPath.getList(
            "data.supportingDocuments.purpose",
            String::class.java
        )
        assertThat(supportingDocumentPurposes).isNotEmpty()
    }

    @Then("the returned list of required supporting document contains ([A-Z_]+) with the document types: ([a-zA-Z,]+)$")
    fun the_returned_list_of_required_supporting_documents(purpose: String, documentTypeCodes: String) {
        val response = ctx.lastResponse ?: Assertions.fail("lastResponse is null")
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body.jsonPath()
        val supportDocumentList: List<TestSupportingDocuments> = jsonPath.getList("data.supportingDocuments", TestSupportingDocuments::class.java)
        val supportDocumentPurpose = supportDocumentList.find { it.purpose == purpose }
        assertThat(supportDocumentPurpose).isNotNull
        val actualCodes: List<String?> = supportDocumentPurpose?.documentTypeOptions?.map { it.code } ?: emptyList()
        val expectedCodes: List<String> = documentTypeCodes.split(",")
        assertThat(actualCodes).containsExactlyInAnyOrderElementsOf(expectedCodes)
    }
}