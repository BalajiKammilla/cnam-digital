package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationStatus
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.tracing.TraceId
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "VerificationOutboxTask")
class VerificationOutboxTaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: VerificationOutboxTask.ID,
    @Enumerated
    @Column(nullable = false, updatable = false)
    override val type: VerificationType,
    @Enumerated
    @Column(nullable = false)
    override var status: VerificationStatus,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(
        name = ENROLMENT_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_verification_outbox_task_enrolment"),
        nullable = false,
        updatable = false
    )
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,
    @Column(name = ENROLMENT_ID, nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    val enrolmentId: PartialEnrolment.ID,
    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime,
    override var processingId: TraceId?,
    override var lastProcessedAt: LocalDateTime?,
    @Column(length = 1000)
    @JsonIgnore
    override var lastFailureReason: String?,
    @Column(nullable = false)
    override var failureCount: Int,
    @JsonIgnore
    override var maxRetryCount: Int,
    override val expiresAt: LocalDateTime?,
    override var nextProcessingAt: LocalDateTime?,
    @Column(nullable = false)
    override val priority: Int
) : VerificationOutboxTask {
    constructor(
        type: VerificationType,
        enrolment: PartialEnrolmentEntity,
        createdAt: LocalDateTime,
    ) : this(
        id = VerificationOutboxTask.ID.NULL,
        type = type,
        status = VerificationStatus.PENDING,
        enrolment = enrolment,
        enrolmentId = enrolment.id,
        createdAt = createdAt,
        processingId = null,
        lastProcessedAt = null,
        lastFailureReason = null,
        failureCount = 0,
        maxRetryCount = 3,
        expiresAt = null,
        nextProcessingAt = createdAt,
        priority = 1,
    )

    override fun updateProcessingId(processingId: TraceId?) {
        this.processingId = processingId
    }

    companion object {
        const val ENROLMENT_ID = "enrolment_id"
    }
}
