package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.persistence.OptimisticLockException
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateEnrolmentVerificationTaskPendingCount(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val updateEnrolmentVerifiedAt: UpdateEnrolmentVerifiedAt,
) {
    @Throws(OptimisticLockException::class)
    operator fun invoke(enrolment: PartialEnrolmentEntity, delta: Int) {
        logger.info("updating verification task pending count -- delta=$delta")
        enrolment.verificationTaskPendingCount += delta

        val verificationTaskPendingCount = enrolment.verificationTaskPendingCount
        logger.info("verification task pending count is '$verificationTaskPendingCount'")
        if (verificationTaskPendingCount == 0) {
            updateEnrolmentVerifiedAt(enrolment)
        }
        jpaOperations.persist(enrolment)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateEnrolmentVerificationTaskPendingCount::class.java)
    }
}