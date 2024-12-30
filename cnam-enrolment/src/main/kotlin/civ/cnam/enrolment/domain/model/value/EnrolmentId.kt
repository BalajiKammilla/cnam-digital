package civ.cnam.enrolment.domain.model.value

import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

@JvmInline
/** */
value class EnrolmentId(@JsonValue val value: String) {
    companion object {
        //private val idGenerator = DateTimePrefixedRandomNumberGenerator()
        fun generate(): EnrolmentId = EnrolmentId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}
