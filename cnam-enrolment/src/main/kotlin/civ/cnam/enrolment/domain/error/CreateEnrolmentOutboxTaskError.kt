package civ.cnam.enrolment.domain.error

import dev.dry.common.error.CodedError

sealed interface CreateEnrolmentOutboxTaskError : CodedError {
    interface EnrolmentNotFound : CreateEnrolmentOutboxTaskError
    interface EnrolmentIncomplete : CreateEnrolmentOutboxTaskError
}