package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.DedupeReviewTaskEntity
import civ.cnam.enrolment.adaptor.model.entity.IdentityDocumentReviewTaskEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.adaptor.model.entity.ReviewTaskEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask.CreateReviewTaskError
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask.DedupeNotFound
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.common.time.TimeProvider
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CreateReviewTaskJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
    private val updateEnrolmentReviewTaskPendingCount: UpdateEnrolmentReviewTaskPendingCount,
) : CreateReviewTask {
    @Transactional
    override fun invoke(
        enrolmentId: EnrolmentId,
        type: ReviewTaskType
    ): Either<CreateReviewTaskError, Unit> {
        logger.info("creating review task of type '$type' for enrolment '$enrolmentId'")

        val enrolment = findPartialEnrolment(enrolmentId) { EnrolmentNotFound }
            .fold({ return Either.left(it) }, { it })

        return createReviewTask(enrolment, type)
    }

    @Transactional
    override operator fun invoke(id: PartialEnrolment.ID, type: ReviewTaskType): Either<CreateReviewTaskError, Unit> {
        logger.info("creating review task of type '$type' for enrolment '$id'")

        val enrolment = findPartialEnrolment(id) { EnrolmentNotFound }
            .fold({ return Either.left(it) }, { it })

        return createReviewTask(enrolment, type)
    }

    private fun createReviewTask(
        enrolment: PartialEnrolmentEntity,
        type: ReviewTaskType
    ): Either<CreateReviewTaskError, Unit> {
        val reviewTask = constructReviewTask(enrolment, type)
            .fold({ return Either.left(it) }, { it })

        jpaOperations.persist(reviewTask)

        updateEnrolmentReviewTaskPendingCount(enrolment, 1)

        logger.info("created review task")

        return Either.right(Unit)
    }

    private fun constructReviewTask(
        enrolment: PartialEnrolmentEntity,
        type: ReviewTaskType
    ): Either<CreateReviewTaskError, ReviewTaskEntity> = when (type) {
        ReviewTaskType.IDENTITY_DOCUMENT -> {
            Either.right(
                IdentityDocumentReviewTaskEntity(
                    createdAt = timeProvider.now(),
                    enrolment = enrolment,
                )
            )
        }
        ReviewTaskType.DEDUPE -> {
            val dedupeMatch = enrolment.dedupeMatch
            if (dedupeMatch != null) {
                Either.right(
                    DedupeReviewTaskEntity(
                        createdAt = timeProvider.now(),
                        enrolment = enrolment,
                        dedupeMatch = dedupeMatch
                    )
                )
            } else {
                Either.left(DedupeNotFound)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CreateReviewTaskJpa::class.java)
    }
}
