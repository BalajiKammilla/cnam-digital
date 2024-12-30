package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.time.SqlDateTimeRange
import java.time.LocalDate
import java.time.LocalDateTime

class ReviewTaskFilter(
    val dateRangeProperty: DateRangeProperty? = null,
    val dateRange: ClosedRange<LocalDateTime>? = null,
    val status: ReviewTask.ReviewTaskStatus?,
    val enrolmentId: EnrolmentId? = null,
) {
    constructor(
        dateRangeProperty: DateRangeProperty? = null,
        fromDate: LocalDate? = null,
        toDate: LocalDate? = null,
        status: ReviewTask.ReviewTaskStatus? = null,
        enrolmentId: EnrolmentId? = null,
    ): this(
        dateRangeProperty = dateRangeProperty,
        dateRange = SqlDateTimeRange.from(fromDate, toDate),
        status = status,
        enrolmentId = enrolmentId,
    )

    enum class DateRangeProperty {
        CREATED_AT,
        COMPLETED_AT,
    }
}
