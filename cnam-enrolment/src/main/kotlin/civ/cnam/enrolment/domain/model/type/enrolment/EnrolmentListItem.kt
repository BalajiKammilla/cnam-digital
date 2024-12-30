package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import java.time.LocalDate
import java.time.LocalDateTime

class EnrolmentListItem(
    val enrolmentId: String,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val verifiedAt: LocalDateTime?,
    val processedAt: LocalDateTime?,
    val approvalStatus: ApprovalStatus?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: LocalDate?,
    val nationality: IsoAlpha3CountryCode?,
    val personCategory: PersonCategoryCode?,
)
