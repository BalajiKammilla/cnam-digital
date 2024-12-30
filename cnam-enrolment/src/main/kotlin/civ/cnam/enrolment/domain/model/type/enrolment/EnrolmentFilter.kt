package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import dev.dry.common.time.SqlDateTimeRange
import dev.dry.core.data.model.value.MobileNumber
import java.time.LocalDate
import java.time.LocalDateTime

class EnrolmentFilter(
    val dateRangeProperty: DateRangeProperty? = null,
    val dateRange: ClosedRange<LocalDateTime>? = null,
    val gender: GenderCode? = null,
    val ageRange: ClosedRange<Int>? = null,
    val nationality: IsoAlpha3CountryCode? = null,
    val personCategory: PersonCategoryCode? = null,
    val location: SubPrefectureCode? = null,
    val completed: Boolean? = null,
    val approvalRequired: Boolean? = null,
    val approvalStatus: ApprovalStatus? = null,
    val processed: Boolean? = null,
    val firstNames: String? = null,
    val lastName: String? = null,
    val mobileNumber: MobileNumber? = null,
    val emailAddress: EmailAddress? = null,
    val enrolmentId: EnrolmentId? = null,
) {
    constructor(
        dateRangeProperty: DateRangeProperty? = null,
        fromDate: LocalDate? = null,
        toDate: LocalDate? = null,
        gender: GenderCode? = null,
        location: SubPrefectureCode? = null,
        fromAge: Int? = null,
        toAge: Int? = null,
        nationality: IsoAlpha3CountryCode? = null,
        personCategory: PersonCategoryCode? = null,
        completed: Boolean? = null,
        approvalRequired: Boolean? = null,
        approvalStatus: ApprovalStatus? = null,
        processed: Boolean? = null,
        firstNames: String? = null,
        lastName: String? = null,
        mobileNumber: MobileNumber? = null,
        emailAddress: EmailAddress? = null,
        enrolmentId: EnrolmentId? = null,
    ): this(
        dateRangeProperty = dateRangeProperty,
        dateRange = SqlDateTimeRange.from(fromDate, toDate),
        gender = gender,
        location = location,
        ageRange = if (fromAge != null || toAge != null) (fromAge ?: toAge)!!..(fromAge ?: toAge)!! else null,
        nationality = nationality,
        personCategory = personCategory,
        completed = completed,
        approvalRequired = approvalRequired,
        approvalStatus = approvalStatus,
        processed = processed,
        firstNames = firstNames,
        lastName = lastName,
        mobileNumber = mobileNumber,
        emailAddress = emailAddress,
        enrolmentId = enrolmentId,
    )

    enum class DateRangeProperty {
        STARTED_AT,
        COMPLETED_AT,
        APPROVAL_STATUS_UPDATED_AT,
        PROCESSING_COMPLETED_AT,
        DATE_OF_BIRTH
    }
}
