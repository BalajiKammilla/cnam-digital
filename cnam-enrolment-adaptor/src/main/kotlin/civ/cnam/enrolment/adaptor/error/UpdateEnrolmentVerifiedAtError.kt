package civ.cnam.enrolment.adaptor.error

import civ.cnam.enrolment.domain.error.CreateEnrolmentOutboxTaskError
import dev.dry.common.error.CodedError

sealed interface UpdateEnrolmentVerifiedAtError : CodedError {
    interface EnrolmentNotFound : UpdateEnrolmentVerifiedAtError, CreateEnrolmentOutboxTaskError.EnrolmentNotFound
    interface EnrolmentIncomplete : UpdateEnrolmentVerifiedAtError, CreateEnrolmentOutboxTaskError.EnrolmentIncomplete
    interface EnrolmentDetailsNotFound : UpdateEnrolmentVerifiedAtError
    interface FailedToCreateAlert : UpdateEnrolmentVerifiedAtError
}