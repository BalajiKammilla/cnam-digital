package civ.cnam.enrolment.domain.error

import dev.dry.common.error.CodedError

sealed interface GetReviewTaskDetailsError : CodedError {
    interface ReviewTaskNotFound : GetReviewTaskDetailsError
    interface FailedToLoadDocumentAttachment : GetReviewTaskDetailsError
    interface EnrolmentNotCompletedError : GetReviewTaskDetailsError
}