package civ.cnam.enrolment.domain.model.value

@JvmInline
value class ExternalReference(val value: String) {
    override fun toString(): String = value
}
