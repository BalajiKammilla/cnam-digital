package civ.cnam.enrolment.function

import civ.cnam.enrolment.domain.function.MapAgeRangeToDateRange
import dev.dry.common.time.TimeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.stream.Stream

class MapAgeRangeToDateRangeTest {
    private val clock = Clock.fixed(
        LocalDate.of(CLOCK_YEAR, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
        ZoneId.of("UTC")
    )
    private val unit = MapAgeRangeToDateRange(TimeProvider(clock))

    @ParameterizedTest
    @MethodSource
    fun mapAgeRangeToDateRange(args: Pair<ClosedRange<Int>, ClosedRange<LocalDate>>) {
        val (ageRange, dateOfBirthRange) = args
        assertEquals(dateOfBirthRange, unit(ageRange))
    }

    companion object {
        private const val CLOCK_YEAR = 2023

        private fun pastDate(years: Int): LocalDate = LocalDate.of(CLOCK_YEAR - years, 1, 1)

        @JvmStatic
        fun mapAgeRangeToDateRange(): Stream<Pair<ClosedRange<Int>, ClosedRange<LocalDate>>> {
            return Stream.of(
                1..1 to pastDate(1) .. pastDate(1),
                1..7 to pastDate(1) .. pastDate(7),
                15..25 to pastDate(15) .. pastDate(25),
            )
        }
    }
}
