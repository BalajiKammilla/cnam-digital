package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationStatus
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTaskResult
import dev.dry.common.error.CodedError
import dev.dry.core.tracing.TraceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "VerificationOutboxTaskResult")
class VerificationOutboxTaskResultEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: VerificationOutboxTaskResult.ID,
    @Column(nullable = false, updatable = false)
    override val processingId: TraceId?,
    @Column(nullable = false, updatable = false)
    override val type: VerificationType,
    @Column(nullable = false)
    override var status: VerificationStatus,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "task_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_verification_outbox_task_result_task"),
        nullable = false,
        updatable = false,
    )
    override val task: VerificationOutboxTaskEntity,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "enrolment_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_verification_outbox_task_result_enrolment"),
        nullable = false,
        updatable = false,
    )
    override val enrolment: PartialEnrolmentEntity,
    @Column(nullable = false, updatable = false)
    override val processedAt: LocalDateTime,
    failureReason: String?,
    failureError: String?
) : VerificationOutboxTaskResult {

    @Column(updatable = false)
    override val failureReason: String? = failureReason?.let { it.substring(0, minOf(it.length, 255) - 1) }

    @Column(updatable = false, length = 1024)
    override val failureError: String? = failureError?.let { it.substring(0, minOf(it.length, 1024) - 1) }

    constructor(
        processingId: TraceId?,
        type: VerificationType,
        status: VerificationStatus,
        task: VerificationOutboxTaskEntity,
        enrolment: PartialEnrolmentEntity,
        processedAt: LocalDateTime,
        error: CodedError?,
    ) : this(
        id = VerificationOutboxTaskResult.ID.NULL,
        processingId = processingId,
        type = type,
        status = status,
        task = task,
        enrolment = enrolment,
        processedAt = processedAt,
        failureReason = error?.message,
        failureError = error?.toString(),
    )
}