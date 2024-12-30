package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.ReviewTask
import java.time.LocalDateTime

class ReviewTaskListItem(
    val id: ReviewTask.ID,
    val type: ReviewTask.ReviewTaskType,
    val status: ReviewTask.ReviewTaskStatus,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime? = null,
    val note: String? = null,
) {
    constructor(reviewTask: ReviewTask): this(
        id = reviewTask.id,
        type = reviewTask.type,
        status = reviewTask.status,
        createdAt = reviewTask.createdAt,
        completedAt = reviewTask.completedAt,
        note = reviewTask.note,
    )
}
