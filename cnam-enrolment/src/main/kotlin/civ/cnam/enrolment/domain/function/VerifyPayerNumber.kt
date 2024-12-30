package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.domain.model.value.CNAMNumber

interface VerifyPayerNumber {
    operator fun invoke(number: CNAMNumber): Boolean
}