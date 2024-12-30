package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.core.data.model.value.MobileNumber
import java.time.LocalDate

class EnrolmentActionFilter(
    val toCreatedAt: LocalDate? = null,
    val fromCreatedAt: LocalDate? = null,
    val kind: EnrolmentActionKind? = null,
    val mobileNumber: MobileNumber? = null,
    val enrolmentId: EnrolmentId? = null
)
