package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.adaptor.model.entity.DedupeReviewTaskEntity.Companion.DEDUPE_ID
import civ.cnam.enrolment.adaptor.model.entity.ReviewTaskEntity.Companion.ENROLMENT_ID
import civ.cnam.enrolment.domain.model.entity.DedupeReviewTask
import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.IdentityDocumentReviewTask
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Transient
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity(name = "ReviewTask")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType = DiscriminatorType.STRING)
sealed class ReviewTaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: ReviewTask.ID,
    @Column(nullable = false, insertable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    override val type: ReviewTask.ReviewTaskType,
    @Column(nullable = false, updatable = true)
    @Enumerated
    override var status: ReviewTask.ReviewTaskStatus,
    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime,
    @Column
    override var completedAt: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = COMPLETED_BY_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_review_task_completed_by"),
    )
    override var completedBy: EnrolmentUserEntity? = null,
    @Column(length = 1000)
    override var note: String? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = ENROLMENT_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_review_task_enrolment"),
    )
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,
    @Column(name = ENROLMENT_ID, nullable = false, insertable = false, updatable = false)
    val enrolmentId: PartialEnrolment.ID,
) : ReviewTask {
    constructor(
        type: ReviewTask.ReviewTaskType,
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity
    ): this(
        id = ReviewTask.ID.NULL,
        type = type,
        status = ReviewTask.ReviewTaskStatus.PENDING,
        createdAt = createdAt,
        enrolment = enrolment,
        enrolmentId = enrolment.id,
    )

    companion object {
        const val COMPLETED_BY_ID = "completed_by_id"
        const val ENROLMENT_ID = "enrolment_id"
    }
}

@Entity(name="IdentityDocumentReviewTask")
@DiscriminatorValue("IDENTITY_DOCUMENT")
class IdentityDocumentReviewTaskEntity(
    createdAt: LocalDateTime,
    enrolment: PartialEnrolmentEntity,
) : ReviewTaskEntity(
    type = ReviewTask.ReviewTaskType.IDENTITY_DOCUMENT,
    createdAt = createdAt,
    enrolment = enrolment,
), IdentityDocumentReviewTask {
    @get:Transient
    override val identityDocument: IdentityDocument
        get() = enrolment.identityDocument
            ?: throw IllegalStateException("identityDocument of IdentityDocumentReviewTask is null")
}

@Entity(name="DedupeReviewTask")
@DiscriminatorValue("DEDUPE")
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_review_task_dedupe_enrolment",
            columnNames = [DEDUPE_ID, ENROLMENT_ID]
        )
    ]
)
class DedupeReviewTaskEntity(
    createdAt: LocalDateTime,
    enrolment: PartialEnrolmentEntity,
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = DEDUPE_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_review_task_dedupe"),
    )
    @JsonIgnore
    override val dedupeMatch: DedupeMatchEntity,
) : ReviewTaskEntity(
    type = ReviewTask.ReviewTaskType.DEDUPE,
    createdAt = createdAt,
    enrolment = enrolment,
), DedupeReviewTask {
    companion object {
        const val DEDUPE_ID = "dedupe_id"
    }
}