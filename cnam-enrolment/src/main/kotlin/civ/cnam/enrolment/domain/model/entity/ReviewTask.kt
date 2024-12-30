package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface ReviewTask {
    val id: ID
    val type: ReviewTaskType
    val status: ReviewTaskStatus
    val createdAt: LocalDateTime
    val enrolment: PartialEnrolment
    val completedAt: LocalDateTime?
    val completedBy: EnrolmentUser?
    val note: String?

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        companion object {
            val NULL = ID(0)
        }
    }

    enum class ReviewTaskType { IDENTITY_DOCUMENT, DEDUPE }

    enum class ReviewTaskStatus { PENDING, APPROVED, REJECTED }
}

interface IdentityDocumentReviewTask : ReviewTask {
    val identityDocument: IdentityDocument
}

interface DedupeReviewTask : ReviewTask {
    val dedupeMatch: DedupeMatch
}
