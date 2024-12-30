package civ.cnam.enrolment.domain.model.referencedata

@JvmInline
value class GenderCode(val value: String) {
    companion object {
        val MALE = GenderCode("M")
        val FEMALE = GenderCode("F")
    }
}
