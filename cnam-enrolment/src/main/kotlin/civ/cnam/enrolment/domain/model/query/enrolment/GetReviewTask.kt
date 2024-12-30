package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.ReviewTaskNotFound
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import dev.dry.common.function.Either

interface GetReviewTask {
    operator fun invoke(id: ReviewTask.ID): Either<ReviewTaskNotFound, ReviewTask>
}
