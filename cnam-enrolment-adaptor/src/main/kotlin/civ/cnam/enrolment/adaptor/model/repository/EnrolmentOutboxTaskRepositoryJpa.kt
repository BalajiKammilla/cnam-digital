package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentOutboxTaskEntity
import civ.cnam.enrolment.adaptor.model.entity.EnrolmentOutboxTaskResultEntity
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.FindPartialEnrolmentJpa
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.CreateEnrolmentOutboxTaskError
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentIncomplete
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask
import civ.cnam.enrolment.domain.model.repository.EnrolmentOutboxTaskRepository
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.time.TimeProvider
import dev.dry.core.data.pagination.Page
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentOutboxTaskRepositoryJpa(
    private val timeProvider: TimeProvider,
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
) : EnrolmentOutboxTaskRepository {
    @Transactional
    override fun createTasks(enrolmentId: EnrolmentId): Either<CreateEnrolmentOutboxTaskError, Unit> {
        val enrolment = findPartialEnrolment.orNull(enrolmentId)
            ?: return Either.left(EnrolmentNotFound)
        val identityDocument = enrolment.identityDocument
            ?: return Either.left(EnrolmentIncomplete)
        val createdAt = timeProvider.now()
        val tasks = mutableListOf<EnrolmentOutboxTaskEntity>()
        tasks.add(EnrolmentOutboxTaskEntity(createdAt, enrolment))
        var sequenceNumber = 0
        (1 .. identityDocument.pageCount).forEach { pageNumber ->
            tasks.add(
                EnrolmentOutboxTaskEntity(
                    createdAt,
                    enrolment,
                    identityDocument,
                    pageNumber = pageNumber,
                    sequenceNumber = ++sequenceNumber
                )
            )
        }
        enrolment.supportingDocuments.forEach { supportingDocument ->
            (1..supportingDocument.pageCount).forEach { pageNumber ->
                tasks.add(
                    EnrolmentOutboxTaskEntity(
                        createdAt,
                        enrolment,
                        supportingDocument,
                        pageNumber = pageNumber,
                        sequenceNumber = ++sequenceNumber
                    )
                )
            }

        }
        jpaOperations.persist(tasks)
        return Either.right(Unit)
    }

    @Transactional
    override fun createTaskResultAndUpdateTask(
        enrolmentOutboxTaskId: EnrolmentOutboxTask.ID,
        processingId: String,
        startedAt: LocalDateTime,
        completedAt: LocalDateTime,
        error: CodedError?
    ): Either<CodedError, Unit> {
        val jpql = "SELECT eot FROM EnrolmentOutboxTask eot WHERE eot.id = :id"
        val task = jpaOperations.querySingleResultOrNull(EnrolmentOutboxTaskEntity::class, jpql) {
            parameter("id", enrolmentOutboxTaskId.value)
        } ?: return Either.left(EnrolmentErrors.ENROLMENT_OUTBOX_TASK_NOT_FOUND)

        val result = EnrolmentOutboxTaskResultEntity(task, processingId, startedAt, completedAt, error)
        jpaOperations.persist(result)

        if (error == null) {
            jpaOperations.remove(task)
        } else {
            task.lastProcessedAt = completedAt
            task.lastFailureReason = error.message
            ++task.failureCount
            jpaOperations.persist(task)
        }

        return Either.right(Unit)
    }

    override fun findPendingTasks(pageNumber: Int, pageSize: Int): Page<EnrolmentOutboxTask.ID> {
        val jpqlCount = """
            SELECT COUNT(eot) FROM EnrolmentOutboxTask eot 
            WHERE eot.processingId IS NULL 
        """.trimIndent()
        val jpql = """
            SELECT eot FROM EnrolmentOutboxTask eot 
            WHERE eot.processingId IS NULL 
            ORDER BY eot.createdAt DESC, eot.sequenceNumber ASC
        """.trimIndent()
        return jpaOperations.queryPage(
            EnrolmentOutboxTaskEntity::class,
            jpql = jpql,
            jpqlCount = jpqlCount,
            pageNumber = pageNumber,
            pageSize = pageSize,
        ).mapContent { it.id }
    }

    @Transactional
    override fun findAndUpdatePendingTaskForProcessing(
        id: EnrolmentOutboxTask.ID,
        processingId: String
    ): EnrolmentOutboxTaskEntity? {
        val jpqlUpdate = """
            UPDATE EnrolmentOutboxTask SET processingId = :processingId
            WHERE id = :id AND processingId IS NULL
        """.trimIndent()
        val affectedRows = jpaOperations.update(jpqlUpdate) {
            parameter("id", id.value)
            parameter("processingId", processingId)
        }

        if (affectedRows != 1) {
            logger.info("failed to update enrolment outbox task '$id' - affectedRows=$affectedRows")
            return null
        }

        val jpql = "SELECT eot FROM EnrolmentOutboxTask eot WHERE eot.id = :id AND eot.processingId = :processingId"
        return jpaOperations.querySingleResultOrNull(EnrolmentOutboxTaskEntity::class, jpql) {
            parameter("id", id.value)
            parameter("processingId", processingId)
        }?.also {
            // TODO("handle session closed error")
            it.enrolment.dedupeMatch
            it.enrolment.identityDocument
            it.enrolment.enrolmentDetails
            it.enrolment.supportingDocuments.forEach {
                it.documentAttachmentId
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EnrolmentOutboxTaskRepositoryJpa::class.java)
    }
}