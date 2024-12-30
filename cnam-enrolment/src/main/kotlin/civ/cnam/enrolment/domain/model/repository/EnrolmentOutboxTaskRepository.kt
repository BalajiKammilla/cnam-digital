package civ.cnam.enrolment.domain.model.repository

import civ.cnam.enrolment.domain.error.CreateEnrolmentOutboxTaskError
import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.pagination.Page
import java.time.LocalDateTime

interface EnrolmentOutboxTaskRepository {
    fun createTasks(enrolmentId: EnrolmentId): Either<CreateEnrolmentOutboxTaskError, Unit>

    fun createTaskResultAndUpdateTask(
        enrolmentOutboxTaskId: EnrolmentOutboxTask.ID,
        processingId: String,
        startedAt: LocalDateTime,
        completedAt: LocalDateTime,
        error: CodedError?
    ): Either<CodedError, Unit>

    fun findPendingTasks(pageNumber: Int, pageSize: Int): Page<EnrolmentOutboxTask.ID>

    fun findAndUpdatePendingTaskForProcessing(id: EnrolmentOutboxTask.ID, processingId: String): EnrolmentOutboxTask?
}
