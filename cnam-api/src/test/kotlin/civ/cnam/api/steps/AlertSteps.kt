package civ.cnam.api.steps

import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.context.UserScenarioContext
import civ.cnam.api.data.AlertData
import civ.cnam.api.data.TestUser
import civ.cnam.api.resource.Resources
import civ.cnam.api.resource.alert.AlertResource
import civ.cnam.enrolment.constants.RealmNames
import io.cucumber.java.en.Then
import org.assertj.core.api.Assertions.assertThat

class AlertSteps {
    val ctx = ScenarioContext
    val user = UserScenarioContext

    @Then("^an alert with the reason ([A-Z_]+) is sent via the ([A-Z_]+) channel to ([0-9a-zA-Z_.+-]+)$")
    fun then_an_alert_with_the_reason_is_sent_via_the_channel_to(
        reason: String,
        channel: String,
        recipient: String,
    ) {
        val accessToken = createAccessToken(realmName = RealmNames.CNAM)
        val alertResource = AlertResource(accessToken = accessToken)

        val response = alertResource.get(
            pageNumber = 1,
            pageSize = 1000,
            channel = channel,
            recipient = recipient
        )
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(200)

        val jsonPath = response.body.jsonPath()
        val alerts: List<AlertData> = jsonPath.getList("data.content", AlertData::class.java)

        assertThat(alerts.find { it.reason == reason }).isNotNull
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
}
