package civ.cnam.api.configuration

import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import dev.dry.alert.constants.AlertBuildProperty.DRY_ALERT_ENABLED
import dev.dry.alert.constants.AlertConfigurationProperty.DRY_ALERT_JPA_OPERATIONS_NAME
import dev.dry.audit.constants.AuditBuildProperty.DRY_AUDIT_ENABLED
import dev.dry.audit.constants.AuditConfigurationProperty.DRY_AUDIT_JPA_OPERATIONS_NAME
import dev.dry.configuration.constants.ConfigurationBuildProperty.DRY_CONFIGURATION_ENABLED
import dev.dry.configuration.constants.ConfigurationConfigurationProperty.DRY_CONFIGURATION_JPA_OPERATIONS_NAME
import dev.dry.core.jpa.operations.JpaOperations
import dev.dry.quarkus.core.jpa.QuarkusHibernateJpaOperations
import dev.dry.user.constants.UserBuildProperty.DRY_USER_ENABLED
import dev.dry.user.constants.UserConfigurationProperty.DRY_USER_JPA_OPERATIONS_NAME
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.persistence.EntityManager

@Singleton
class JpaOperationsProducer {
    @Produces
    @Named(DEFAULT_JPA_OPERATIONS)
    @Singleton
    fun defaultJpaOperations(entityManager: EntityManager): JpaOperations {
        return QuarkusHibernateJpaOperations(entityManager)
    }

    @Produces
    @Named(DRY_CONFIGURATION_JPA_OPERATIONS_NAME)
    @Singleton
    @IfBuildProperty(name = DRY_CONFIGURATION_ENABLED, stringValue = "true")
    fun configurationJpaOperations(entityManager: EntityManager): JpaOperations {
        return QuarkusHibernateJpaOperations(entityManager)
    }

    @Produces
    @Named(DRY_AUDIT_JPA_OPERATIONS_NAME)
    @Singleton
    @IfBuildProperty(name = DRY_AUDIT_ENABLED, stringValue = "true")
    fun auditJpaOperations(entityManager: EntityManager): JpaOperations {
        return QuarkusHibernateJpaOperations(entityManager)
    }

    @Produces
    @Named(DRY_ALERT_JPA_OPERATIONS_NAME)
    @Singleton
    @IfBuildProperty(name = DRY_ALERT_ENABLED, stringValue = "true")
    fun alertJpaOperations(entityManager: EntityManager): JpaOperations {
        return QuarkusHibernateJpaOperations(entityManager)
    }

    @Produces
    @Named(DRY_USER_JPA_OPERATIONS_NAME)
    @Singleton
    @IfBuildProperty(name = DRY_USER_ENABLED, stringValue = "true")
    fun userJpaOperations(entityManager: EntityManager): JpaOperations {
        return QuarkusHibernateJpaOperations(entityManager)
    }
}