package civ.cnam.api.suites

import civ.cnam.api.PostgresTestResource
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.profiles.ApiTestProfile
import io.cucumber.java.After
import io.cucumber.java.Scenario
import io.quarkiverse.cucumber.CucumberQuarkusTest
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.TestProfile

@TestProfile(ApiTestProfile::class)
@QuarkusTestResource(PostgresTestResource::class)
class ApiTestSuite : CucumberQuarkusTest() {
    @After
    fun afterScenario(scenario: Scenario) {
        ScenarioContext.reset()
    }
}
