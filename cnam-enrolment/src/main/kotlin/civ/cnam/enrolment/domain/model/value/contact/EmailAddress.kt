package civ.cnam.enrolment.domain.model.value.contact

import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

fun interface EmailAddressParser {
    fun parser(string: String): Either<CodedError, EmailAddress>
}

@JvmInline
value class EmailAddress(val value: String)
