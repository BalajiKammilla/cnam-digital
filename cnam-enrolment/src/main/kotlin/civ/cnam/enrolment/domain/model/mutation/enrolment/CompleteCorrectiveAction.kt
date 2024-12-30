package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.CORRECTIVE_ACTION_NOT_FOUND
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

interface CompleteCorrectiveAction {
    operator fun invoke(
        enrolmentId: PartialEnrolment.ID,
        type: CorrectiveAction.CorrectiveActionType
    ): Either<CorrectiveActionNotFound, Unit>

    operator fun invoke(id: CorrectiveAction.ID): Either<CorrectiveActionNotFound, Unit>

    sealed class UpdateCorrectiveActionError(error: CodedError) : CodedError.DefaultCodedError(error)
    object CorrectiveActionNotFound : UpdateCorrectiveActionError(CORRECTIVE_ACTION_NOT_FOUND)
}