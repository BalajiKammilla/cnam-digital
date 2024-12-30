package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask
import com.fasterxml.jackson.annotation.JsonIgnore
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

@Entity(name = "EnrolmentOutboxTask")
class EnrolmentOutboxTaskEntity(
    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime,
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
    override val identityDocument: IdentityDocumentEntity? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "supporting_document_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_outbox_supporting_document"),
    )
    @JsonIgnore
    override val supportingDocument: SupportingDocumentEntity? = null,
    override val pageNumber: Int = 0,
    override val sequenceNumber: Int = 0,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: EnrolmentOutboxTask.ID = EnrolmentOutboxTask.ID(0),
    override val processingId: String? = null,
    override var lastProcessedAt: LocalDateTime? = null,
    override var lastFailureReason: String? = null,
    @Column(nullable = false)
    override var failureCount: Int = 0
) : EnrolmentOutboxTask {
    constructor(
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity,
    ): this(createdAt, enrolment, null, null)

    constructor(
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity,
        identityDocument: IdentityDocumentEntity,
        pageNumber: Int,
        sequenceNumber: Int,
    ): this(
        createdAt,
        enrolment,
        identityDocument,
        null,
        pageNumber = pageNumber,
        sequenceNumber = sequenceNumber
    )

    constructor(
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity,
        supportingDocument: SupportingDocumentEntity,
        pageNumber: Int,
        sequenceNumber: Int,
    ): this(
        createdAt,
        enrolment,
        null,
        supportingDocument,
        pageNumber = pageNumber,
        sequenceNumber = sequenceNumber
    )
}
