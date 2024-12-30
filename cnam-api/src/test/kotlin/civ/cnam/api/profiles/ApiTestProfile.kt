package civ.cnam.api.profiles

class ApiTestProfile : AbstractTestProfile() {
    override fun setConfigOverrides(configOverrides: MutableMap<String, String>) {
        configOverrides["dry.core.object-store.adaptor"] = "memory"
        configOverrides["dry.user.api.enabled"] = "true"
        configOverrides["dry.user.management-api.enabled"] = "true"
        configOverrides["cnam.enrolment.enabled"] = "true"
        configOverrides["cnam.enrolment.api.enabled"] = "true"
        configOverrides["cnam.enrolment.management-api.enabled"] = "true"
    }

    override fun addConfigProfiles(configProfiles: MutableSet<String>) {
        configProfiles.add("api")
        configProfiles.add("management-api")
        configProfiles.add("scheduled-job")
    }
}
