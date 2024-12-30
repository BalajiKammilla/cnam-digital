package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.model.entity.CorrectiveAction
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.core.data.model.value.MobileNumber

interface FilterCorrectiveActions {
    operator fun invoke(
        enrolmentId: EnrolmentId,
        status: CorrectiveActionStatus?,
        mobileNumber: MobileNumber,
    ): List<CorrectiveAction>
}
