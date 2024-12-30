package civ.cnam.api.steps

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.data.TestUser
import civ.cnam.api.resource.Resources
import civ.cnam.api.resource.enrolment.VerificationTaskResource
import civ.cnam.enrolment.constants.RealmNames
import civ.cnam.enrolment.domain.model.type.enrolment.VerificationTaskListItem
import io.cucumber.java.en.Then
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.awaitility.kotlin.withPollDelay
import org.awaitility.kotlin.withPollInterval
import java.time.Duration

class VerificationTaskSteps {
    private val ctx = ScenarioContext
    private val user = UserScenarioContext
    private val enrolment = EnrolmentContext

    @Then("verification tasks are processed")
    fun then_verification_tasks_are_processed() {
        val enrolmentId  = enrolment.enrolmentId
        await withPollInterval FIVE_HUNDRED_MILLISECONDS withPollDelay FIVE_HUNDRED_MILLISECONDS atMost FIVE_SECONDS until {
            val accessToken = createAccessToken(realmName = RealmNames.CNAM)
            val verificationTaskResource = VerificationTaskResource(accessToken = accessToken)

            val response = verificationTaskResource.getList(
                pageNumber = 1,
                pageSize = 1000,
                enrolmentId = enrolmentId,
            )
            assertThat(response).isNotNull
            assertThat(response.statusCode).isEqualTo(200)

            val jsonPath = response.body.jsonPath()
            val taskList: List<VerificationTaskListItem> = jsonPath.getList(
                "data.page.content",
                VerificationTaskListItem::class.java
            )
            assertThat(taskList).isNotEmpty

            // TODO("add status to VerificationOutboxTask")
            taskList.all { it.processingId != null }
        }
    }

    private fun createAccessToken(
        realmName: String? = null,
        userName: String? = null,
        permissionList: List<TestUser.HasPermission> = emptyList(),
    ): String {
        val testUser = TestUser.select(realmName?.lowercase(), userName, permissionList)
        val response = Resources.accessToken.create(userName = testUser.userName, password = testUser.password)
        assertThat(response.statusCode).isEqualTo(200);
        val jsonPath = response.body().jsonPath()
        assertThat(jsonPath.getBoolean("data.otpRequired")).isFalse()
        val accessToken = jsonPath.getString("data.accessToken")
        assertThat(accessToken).isNotNull()
        return accessToken
    }

    companion object {
        private val FIVE_HUNDRED_MILLISECONDS = Duration.ofMillis(500)
        private val FIVE_SECONDS = Duration.ofSeconds(120)
    }
}
