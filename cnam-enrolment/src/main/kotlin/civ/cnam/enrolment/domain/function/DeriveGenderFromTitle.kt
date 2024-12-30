package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode

fun deriveGenderFromTitle(titleCode: TitleCode): GenderCode {
    return if (titleCode.value == "M") GenderCode.MALE else GenderCode.FEMALE
}