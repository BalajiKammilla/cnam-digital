package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationStatus
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import java.time.LocalDateTime

class VerificationTaskListItem(
    val id: Long,
    val createdAt: LocalDateTime,
    val priority: Int,
    val expiresAt: LocalDateTime?,
    val processingId: String?,
    val lastProcessedAt: LocalDateTime?,
    val nextProcessingAt: LocalDateTime?,
    val failureCount: Int,
    val type: VerificationType,
    val status: VerificationStatus,
) {
    constructor(task: VerificationOutboxTask): this(
        id = task.id.value,
        createdAt = task.createdAt,
        priority = task.priority,
        expiresAt = task.expiresAt,
        processingId = task.processingId?.value,
        lastProcessedAt = task.lastProcessedAt,
        nextProcessingAt = task.nextProcessingAt,
        failureCount = task.failureCount,
        type = task.type,
        status = task.status,
    )
}
