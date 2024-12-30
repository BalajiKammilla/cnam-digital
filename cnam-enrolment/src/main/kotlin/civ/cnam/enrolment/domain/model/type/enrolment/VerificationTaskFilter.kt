package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.time.SqlDateTimeRange
import java.time.LocalDate
import java.time.LocalDateTime

class VerificationTaskFilter(
    val dateRangeProperty: DateRangeProperty? = null,
    val dateRange: ClosedRange<LocalDateTime>? = null,
    val type: VerificationType?,
    val enrolmentId: EnrolmentId? = null,
) {
    constructor(
        dateRangeProperty: DateRangeProperty? = null,
        fromDate: LocalDate? = null,
        toDate: LocalDate? = null,
        type: VerificationType? = null,
        enrolmentId: EnrolmentId? = null,
    ): this(
        dateRangeProperty = dateRangeProperty,
        dateRange = SqlDateTimeRange.from(fromDate, toDate),
        type = type,
        enrolmentId = enrolmentId,
    )

    enum class DateRangeProperty {
        CREATED_AT,
        LAST_PROCESSED_AT,
    }
}
