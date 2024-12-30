package civ.cnam.api.steps.user

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.constants.RealmNames
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.assertj.hasIntEntrySatisfying
import dev.dry.assertj.hasListEntrySatisfying
import dev.dry.assertj.hasStringEntrySatisfying
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.AbstractMapAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.MapAssert

class GetCnamUserListSteps {
    private val ctx = ScenarioContext

    @When("I request the CNAM user list")
    fun when_i_request_the_cnam_user_list() {
        ctx.lastResponse = Resources.cnamUser.getList(pageNumber = PAGE_NUMBER, pageSize = PAGE_SIZE)
    }

    @Then("the CNAM user list is retrieved")
    fun then_the_cnam_user_list_is_retrieved() {
        val response = ctx.lastResponse ?: fail("getListResponse is null")

        assertThat(response.statusCode).isEqualTo(200)
        val jsonPath = response.body().jsonPath()

        assertThat(jsonPath.getMap<String, Any>("data"))
            .`as`("Paginated User Accounts")
            .isNotNull
            .hasIntEntrySatisfying("pageNumber") { assertThat(it).isEqualTo(PAGE_NUMBER) }
            .hasIntEntrySatisfying("pageSize") { assertThat(it).isEqualTo(PAGE_SIZE) }
            .hasIntEntrySatisfying("totalSize") { assertThat(it).isGreaterThan(0) }
            .hasIntEntrySatisfying("totalPages") { assertThat(it).isGreaterThan(0) }
            .hasListEntrySatisfying("content", ::isCnamUserAccountList)
    }

    fun isCnamUserAccountList(userAccountList: List<Map<String, Any>>?) {
        assertThat(userAccountList)
            .`as`("CNAM User Accounts List")
            .isNotNull
            .isNotEmpty
            .allSatisfy(::isCnamUserAccount)
    }



    companion object {
        const val PAGE_NUMBER = 1
        const val PAGE_SIZE = 10
    }
}
