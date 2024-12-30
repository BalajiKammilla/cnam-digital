package civ.cnam.api.suites

import civ.cnam.api.context.ScenarioContext
import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import io.cucumber.java.Scenario
import org.junit.After
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("enrolment")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "civ.cnam.api.steps")
class EnrolmentApiTest {
    @After
    fun afterScenario(scenario: Scenario) {
        ScenarioContext.reset()
    }
}
