package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTaskResult
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.common.error.CodedError
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

@Entity(name = "EnrolmentOutboxTaskResult")
class EnrolmentOutboxTaskResultEntity(
    @Column(nullable = false, updatable = false)
    override val processingId: String,
    @Column(nullable = false, updatable = false)
    override val startedAt: LocalDateTime,
    @Column(nullable = false, updatable = false)
    override val completedAt: LocalDateTime,
    @Column(updatable = false, length = 1000)
    override val failureReason: String?,
    @Column(updatable = false, length = 4000)
    override val failureError: String?,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "enrolment_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_outbox_enrolment"),
        nullable = false
    )
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "identity_document_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_outbox_identity_document"),
    )
    @JsonIgnore
    override val identityDocument: IdentityDocumentEntity?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "supporting_document_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_outbox_supporting_document"),
    )
    @JsonIgnore
    override val supportingDocument: SupportingDocumentEntity?,
    @Column(nullable = false, updatable = false)
    override val pageNumber: Int,
    @Column(nullable = false, updatable = false)
    override val sequenceNumber: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: EnrolmentOutboxTaskResult.ID = EnrolmentOutboxTaskResult.ID(0),
) : EnrolmentOutboxTaskResult {
    constructor(
        task: EnrolmentOutboxTaskEntity,
        processingId: String,
        startedAt: LocalDateTime,
        completedAt: LocalDateTime,
        error: CodedError?
    ): this(
        enrolment = task.enrolment,
        identityDocument = task.identityDocument,
        supportingDocument = task.supportingDocument,
        pageNumber = task.pageNumber,
        sequenceNumber = task.sequenceNumber,
        processingId = processingId,
        startedAt = startedAt,
        completedAt = completedAt,
        failureReason = error?.message,
        failureError = error?.toString(),
    )
}
