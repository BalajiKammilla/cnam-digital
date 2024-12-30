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
class UpdateEnrolmentReviewTaskPendingCount(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val updateEnrolmentVerifiedAt: UpdateEnrolmentVerifiedAt,
) {
    @Throws(OptimisticLockException::class)
    operator fun invoke(enrolment: PartialEnrolmentEntity, delta: Int) {
        logger.info("updating review task pending count -- delta=$delta")
        enrolment.reviewTaskPendingCount += delta

        val reviewTaskPendingCount = enrolment.reviewTaskPendingCount
        logger.info("review task pending count is '$reviewTaskPendingCount'")
        if (reviewTaskPendingCount == 0) {
            updateEnrolmentVerifiedAt(enrolment)
        }
        jpaOperations.persist(enrolment)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateEnrolmentReviewTaskPendingCount::class.java)
    }
}