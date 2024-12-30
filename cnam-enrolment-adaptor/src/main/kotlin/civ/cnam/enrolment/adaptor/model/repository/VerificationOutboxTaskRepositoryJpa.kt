package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.adaptor.model.entity.VerificationOutboxTaskEntity
import civ.cnam.enrolment.adaptor.model.entity.VerificationOutboxTaskResultEntity
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.FindPartialEnrolmentJpa
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.UpdateEnrolmentVerificationTaskPendingCount
import civ.cnam.enrolment.adaptor.model.type.VerificationOutboxTaskProcessed
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.VerificationOutboxTaskNotFound
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationStatus
import civ.cnam.enrolment.domain.model.repository.VerificationOutboxTaskRepository
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.function.map
import dev.dry.common.time.TimeProvider
import dev.dry.core.jpa.operations.JpaOperations
import dev.dry.core.outbox.OutboxErrors
import dev.dry.core.outbox.jpa.JpaOutboxItemRepository
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class VerificationOutboxTaskRepositoryJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
    private val timeProvider: TimeProvider,
    private val updateEnrolmentVerificationTaskPendingCount: UpdateEnrolmentVerificationTaskPendingCount,
) : JpaOutboxItemRepository<VerificationOutboxTask.ID, Long, VerificationOutboxTaskEntity, VerificationOutboxTaskProcessed>(
    entityClass = VerificationOutboxTaskEntity::class,
    idValueClass = Long::class,
    transformIdValue = { it.value },
    timeProvider = timeProvider,
    jpaOperations = jpaOperations,
    maxAttempts = 3,
    attemptRetryDelay = Duration.ofSeconds(10)
), VerificationOutboxTaskRepository {
    override fun createTasks(
        enrolmentId: EnrolmentId,
        types: Set<VerificationOutboxTask.VerificationType>
    ): Either<EnrolmentNotFound, Unit> {
        logger.info("creating verification tasks for enrolment '$enrolmentId' - $types")
        return findPartialEnrolment(enrolmentId)
            .map { enrolment ->
                val tasks = types.map { type ->
                    VerificationOutboxTaskEntity(
                        type = type,
                        enrolment = enrolment,
                        createdAt = timeProvider.now(),
                    )
                }
                jpaOperations.persist(tasks)
                updateEnrolmentVerificationTaskPendingCount(enrolment, tasks.size)
            }
    }

    private fun createTaskResultAndUpdateTask(
        task: VerificationOutboxTaskEntity,
        processedAt: LocalDateTime,
        nextProcessingAt: LocalDateTime?,
        error: CodedError?
    ): Either<VerificationOutboxTaskNotFound, Unit> {
        val status = if (error == null) VerificationStatus.COMPLETED else VerificationStatus.FAILED
        logger.info("creating verification tasks result for '$status' task '${task.id}'")

        val processingId = task.processingId
        task.nextProcessingAt = nextProcessingAt
        if (nextProcessingAt != null) {
            task.processingId = null
        }
        task.lastProcessedAt = processedAt
        task.lastFailureReason = error?.toString()?.take(1000)
        if (status == VerificationStatus.COMPLETED) {
            task.status = status
        }
        jpaOperations.persist(task)

        val result = VerificationOutboxTaskResultEntity(
            processingId = processingId,
            type = task.type,
            status = status,
            task = task,
            enrolment = task.enrolment,
            processedAt = processedAt,
            error = error,
        )
        jpaOperations.persist(result)

        return right(Unit)
    }

    override fun updateItem(
        item: VerificationOutboxTaskEntity,
        processedAt: LocalDateTime,
        resultData: VerificationOutboxTaskProcessed
    ) {
        createTaskResultAndUpdateTask(
            task = item,
            processedAt = processedAt,
            nextProcessingAt = null,
            error = null
        ).map {
            updateEnrolmentVerificationTaskPendingCount(item.enrolment, -1)
        }
    }

    override fun updateItem(
        item: VerificationOutboxTaskEntity,
        processedAt: LocalDateTime,
        nextProcessingAt: LocalDateTime?,
        failureCount: Int,
        error: OutboxErrors.OutboxProcessingError
    ) {
        item.failureCount = failureCount
        createTaskResultAndUpdateTask(
            task = item,
            processedAt = processedAt,
            nextProcessingAt = nextProcessingAt,
            error = error
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VerificationOutboxTaskRepositoryJpa::class.java)
    }
}
