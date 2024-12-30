package civ.cnam.enrolment.domain.error

import dev.dry.common.error.CodedError

sealed interface UpdateReviewTaskError : CodedError {
    interface InvalidReviewTaskUpdateType : UpdateReviewTaskError
    interface InvalidReviewTaskUpdateStatus : UpdateReviewTaskError
    interface ReviewTaskAlreadyCompleted : UpdateReviewTaskError
    interface ReviewTaskNotFound : UpdateReviewTaskError
    interface EnrolmentNotFound : UpdateReviewTaskError
    interface UserNotFound : UpdateReviewTaskError
    interface EnrolmentDetailsNotFound : UpdateReviewTaskError
    interface FailedToCreateAlert : UpdateReviewTaskError
    interface FailedToCreateCorrectiveAction : UpdateReviewTaskError
}
