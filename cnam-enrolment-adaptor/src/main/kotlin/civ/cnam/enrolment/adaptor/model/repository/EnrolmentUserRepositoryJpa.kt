package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentUserEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.EnrolmentUser
import civ.cnam.enrolment.domain.model.repository.EnrolmentUserRepository
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentUserRepositoryJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) : EnrolmentUserRepository {
    override fun findByMobileNumber(mobileNumber: MobileNumber): EnrolmentUser {
        val jpql = "SELECT eu FROM EnrolmentUser eu WHERE eu.mobileNumber = :mobileNumber"
        return jpaOperations.querySingleResult(EnrolmentUserEntity::class, jpql) {
            parameter("mobileNumber", mobileNumber.value)
        }
    }
}
