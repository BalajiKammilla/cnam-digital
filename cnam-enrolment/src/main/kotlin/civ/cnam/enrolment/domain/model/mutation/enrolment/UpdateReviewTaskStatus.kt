package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.UpdateReviewTaskError
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.UserName

interface UpdateReviewTaskStatus {
    operator fun invoke(
        id: ReviewTask.ID,
        updatedBy: UserName,
        status: ReviewTaskStatus,
        note: String?,
    ): Either<UpdateReviewTaskError, Unit>
}