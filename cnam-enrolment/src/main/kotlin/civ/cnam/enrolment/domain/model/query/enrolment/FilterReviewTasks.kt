package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskFilter
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskListItem
import dev.dry.core.data.pagination.Page

interface FilterReviewTasks {
    operator fun invoke(pageNumber: Int, pageSize: Int, filter: ReviewTaskFilter): Page<ReviewTaskListItem>
}
