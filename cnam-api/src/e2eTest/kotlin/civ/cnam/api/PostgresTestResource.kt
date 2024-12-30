package civ.cnam.api

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.postgresql.Driver
import org.slf4j.LoggerFactory
import java.io.IOException

class PostgresTestResource : QuarkusTestResourceLifecycleManager {
    private var postgres: EmbeddedPostgres? = null

    override fun start(): Map<String, String> {
        val userName = System.getProperty("user.name")
        check("root" != userName) { "Cannot provision Ephemeral Postgres when running as user: $userName" }
        val pg = try {
            EmbeddedPostgres.builder().start()
        } catch (e: IOException) {
            throw RuntimeException("Could not start Ephemeral Postgres", e)
        }
        postgres = pg
        val props: MutableMap<String, String> = HashMap()
        props["quarkus.datasource.jdbc.url"] = pg.getJdbcUrl("postgres")
        props["quarkus.datasource.username"] = "postgres"
        props["quarkus.datasource.password"] = ""
        props["quarkus.datasource.jdbc.driver"] = Driver::class.java.getName()
        return props
    }

    override fun stop() {
        try {
            postgres?.close()
        } catch (e: IOException) {
            LOGGER.warn("Could not stop Ephemeral Postgres", e)
        }
        postgres = null
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PostgresTestResource::class.java)
    }
}