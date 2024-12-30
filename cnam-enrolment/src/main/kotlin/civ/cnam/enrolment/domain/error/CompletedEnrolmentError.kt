package civ.cnam.enrolment.domain.error

import dev.dry.common.error.CodedError

sealed interface CompleteEnrolmentError : CodedError {
    sealed interface EnrolmentNotFoundError : CompleteEnrolmentError
    interface EnrolmentNotFound : EnrolmentNotFoundError
    interface EnrolmentNotFoundForMobileNumber : EnrolmentNotFoundError
    sealed interface EnrolmentNotCompletedError : CompleteEnrolmentError,
        GetReviewTaskDetailsError.EnrolmentNotCompletedError,
        UpdateReviewTaskError
    interface EnrolmentDetailsMissing : EnrolmentNotCompletedError
    interface EnrolmentStatusNotSetToCompleted : EnrolmentNotCompletedError
}
