package civ.cnam.api

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class ApplicationLifecycle {
    private val logger = LoggerFactory.getLogger(ApplicationLifecycle::class.java)

    fun onStart(@Observes ev: StartupEvent?) {
        logger.info("Application starting (event: {})...", ev.hashCode())
    }
}