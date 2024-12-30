package civ.cnam.api

import io.quarkus.test.junit.QuarkusTestProfile

class UserApiTestProfile : QuarkusTestProfile {
    override fun getConfigOverrides(): Map<String, String> {
        return mapOf(
            "dry.user.api.enabled" to "true",
            "dry.user.management-api.enabled" to "true",
        )
    }

    override fun getEnabledAlternatives(): Set<Class<*>> {
        return setOf()
    }

    override fun getConfigProfile(): String {
        return "test,user-api-test-profile,api,management-api"
    }
}
