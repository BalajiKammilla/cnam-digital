package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

interface CreateReviewTask {
    operator fun invoke(enrolmentId: EnrolmentId, type: ReviewTaskType): Either<CreateReviewTaskError, Unit>

    operator fun invoke(id: PartialEnrolment.ID, type: ReviewTaskType): Either<CreateReviewTaskError, Unit>

    sealed class CreateReviewTaskError(error: CodedError) : CodedError.DefaultCodedError(error)
    object EnrolmentNotFound : CreateReviewTaskError(EnrolmentErrors.ENROLMENT_NOT_FOUND)
    object DedupeNotFound : CreateReviewTaskError(EnrolmentErrors.DEDUPE_NOT_FOUND)
}