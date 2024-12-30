package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.user.api.CnamUserCreateOperation.CnamUserCreateRequest
import dev.dry.assertj.hasStringEntrySatisfying
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class CreateCnamUserSteps {
    private val ctx = ScenarioContext
    var createRequestBody: CnamUserCreateRequest?
        get() = ctx[CNAM_USER_CREATE_REQUEST]
        set(createRequestBody) {
            ctx[CNAM_USER_CREATE_REQUEST] = createRequestBody
        }

    @DataTableType
    fun createRequestTransformer(row: Map<String, String>): CnamUserCreateRequest {
        val userRoles = row["userRoles"] ?: throw IllegalStateException("CNAM user userRoles not found in data table")
        return CnamUserCreateRequest(
            emailAddress = row["emailAddress"]
                ?: throw IllegalStateException("CNAM user emailAddress not found in data table"),
            displayName = row["displayName"]
                ?: throw IllegalStateException("CNAM user displayName not found in data table"),
            password = row["password"]
                ?: throw IllegalStateException("CNAM user password not found in data table"),
            userRoles = userRoles.split(",").map { it.trim() }.toSet(),
        )
    }

    @Given("the CNAM user details in the following table:")
    fun given_the_cnam_user_details_in_the_following_table(userDetails: CnamUserCreateRequest) {
        createRequestBody = userDetails
    }

    @When("I create a CNAM user")
    fun when_i_create_a_cnam_user() {
        ctx.lastResponse = Resources.cnamUser.create(createRequestBody ?: fail("CNAM user details not set"))
    }

    @Then("the CNAM user is created")
    fun then_the_cnam_user_is_created() {
        val requestBody = createRequestBody ?: fail("createRequestBody is null")
        val response = ctx.lastResponse ?: fail("lastResponse is null")

        assertThat(response.statusCode).isEqualTo(200)

        val json = response.jsonPath()

        assertThat(json.getMap<String, String>("data.userAccount"))
            .`as`("data.userAccount")
            .isNotNull
            .isNotEmpty
            .satisfies(::isCnamUserAccount)
            .hasStringEntrySatisfying("emailAddress") { assertThat(it).isEqualTo(requestBody.emailAddress) }
            .hasStringEntrySatisfying("displayName") { assertThat(it).isEqualTo(requestBody.displayName) }
            .hasStringEntrySatisfying("lastLoginAt") { assertThat(it).isNull() }

        assertThat(json.getList<String>("data.userAccount.roles.role.name"))
            .`as`("data.userAccount.roles")
            .isNotNull
            .isNotEmpty
            .containsExactlyInAnyOrder(*requestBody.userRoles.toTypedArray())
    }

    companion object {
        const val CNAM_USER_CREATE_REQUEST = "CNAM_USER_CREATE_REQUEST"
    }
}