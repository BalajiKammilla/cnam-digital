package civ.cnam.enrolment.function

import civ.cnam.enrolment.domain.function.deriveGenderFromTitle
import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DeriveGenderFromTitleTest {
    @ParameterizedTest
    @MethodSource
    fun deriveGenderFromTitleTest(args: Pair<TitleCode, GenderCode>) {
        val (titleCode, genderCode) = args
        assertEquals(genderCode, deriveGenderFromTitle(titleCode))
    }

    companion object {
        @JvmStatic
        fun deriveGenderFromTitleTest(): Stream<Pair<TitleCode, GenderCode>> {
            return Stream.of(
                TitleCode.MISTER to GenderCode.MALE,
                TitleCode.MADAM to GenderCode.FEMALE,
                TitleCode.MISS to GenderCode.FEMALE,
            )
        }
    }
}