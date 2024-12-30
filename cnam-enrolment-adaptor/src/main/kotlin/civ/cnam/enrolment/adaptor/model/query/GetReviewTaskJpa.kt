package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.ReviewTaskEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ReviewTaskNotFound
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.query.enrolment.GetReviewTask
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetReviewTaskJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) : GetReviewTask {
    override fun invoke(id: ReviewTask.ID): Either<ReviewTaskNotFound, ReviewTaskEntity> {
        logger.info("retrieving review task '$id'")
        val reviewTask = jpaOperations.querySingleResultOrNull(ReviewTaskEntity::class, GET_REVIEW_TASK_JPQL) {
            parameter("id", id.value)
        } ?: return notFound()

        return completed(reviewTask)
    }

    companion object {
        const val GET_REVIEW_TASK_JPQL = "SELECT rt FROM ReviewTask rt WHERE rt.id = :id"

        private val logger = LoggerFactory.getLogger(GetReviewTaskJpa::class.java)

        private fun notFound(): Either.Left<ReviewTaskNotFound> {
            logger.info("retrieving review task failed -- $ReviewTaskNotFound")
            return left(ReviewTaskNotFound)
        }

        private fun completed(reviewTask: ReviewTaskEntity): Either.Right<ReviewTaskEntity> {
            logger.info("retrieving review task completed")
            return right(reviewTask)
        }
    }
}