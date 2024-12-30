package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.error.GetReviewTaskDetailsError
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskData
import dev.dry.common.function.Either

interface GetReviewTaskDetails {
    operator fun invoke(id: ReviewTask.ID): Either<GetReviewTaskDetailsError, ReviewTaskData>
}
