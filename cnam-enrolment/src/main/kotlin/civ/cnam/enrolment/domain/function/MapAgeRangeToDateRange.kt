package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import dev.dry.common.time.TimeProvider
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class MapAgeRangeToDateRange(private val timeProvider: TimeProvider) {
    operator fun invoke(ageRange: ClosedRange<Int>): ClosedRange<LocalDate> {
        val now = timeProvider.now().toLocalDate()
        val start = now.minusYears(ageRange.start.toLong())
        val end = now.minusYears(ageRange.endInclusive.toLong())
        return start .. end
    }
}
