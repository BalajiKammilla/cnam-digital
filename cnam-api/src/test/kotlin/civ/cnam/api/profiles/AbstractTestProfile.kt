package civ.cnam.api.profiles

import io.quarkus.test.junit.QuarkusTestProfile
import org.slf4j.LoggerFactory

abstract class AbstractTestProfile : QuarkusTestProfile {
    override fun getConfigOverrides(): Map<String, String> {
        val configOverrides = mutableMapOf(
            "dry.core.security.data-cipher.kek" to "S48NE/SS5IsVav8xrHdvC/+q2chxCHJCWsZg64CBJd8=",
            "dry.core.object-store.adaptor" to "noop",
        )
        setConfigOverrides(configOverrides)
        return configOverrides.toMap()
    }

    protected open fun setConfigOverrides(configOverrides: MutableMap<String, String>) {
        // no-op
    }

    override fun getEnabledAlternatives(): Set<Class<*>> {
        val enabledAlternatives = mutableSetOf<Class<*>>()
        addEnabledAlternatives(enabledAlternatives)
        return enabledAlternatives.toSet()
    }

    protected open fun addEnabledAlternatives(enabledAlternatives: MutableSet<Class<*>>) {
        // no-op
    }

    override fun getConfigProfile(): String {
        val configProfiles = mutableSetOf("test")
        addConfigProfiles(configProfiles)
        return configProfiles.joinToString(separator = ",").also {
            logger.info("Config Profiles: $it")
        }
    }

    protected open fun addConfigProfiles(configProfiles: MutableSet<String>) {
        // no-op
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AbstractTestProfile::class.java)
    }
}