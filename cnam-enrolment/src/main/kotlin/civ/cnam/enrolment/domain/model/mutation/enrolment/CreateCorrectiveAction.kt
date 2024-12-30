package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_DETAILS_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.FAILED_TO_CREATE_ALERT
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.error.CodedError.DefaultCodedError
import dev.dry.common.function.Either

interface CreateCorrectiveAction {
    operator fun invoke(
        enrolmentId: EnrolmentId,
        type: CorrectiveActionType,
    ): Either<CreateCorrectiveActionError, Unit>

    operator fun invoke(
        enrolmentId: PartialEnrolment.ID,
        type: CorrectiveActionType,
    ): Either<CreateCorrectiveActionError, Unit>

    sealed interface CreateCorrectiveActionError : CodedError
    object EnrolmentNotFound : DefaultCodedError(ENROLMENT_NOT_FOUND), CreateCorrectiveActionError
    object EnrolmentDetailsNotFound : DefaultCodedError(ENROLMENT_DETAILS_NOT_FOUND), CreateCorrectiveActionError
    object FailedToCreateAlert : DefaultCodedError(FAILED_TO_CREATE_ALERT), CreateCorrectiveActionError
}