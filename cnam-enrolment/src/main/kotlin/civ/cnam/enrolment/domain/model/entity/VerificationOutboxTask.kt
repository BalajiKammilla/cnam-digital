package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import dev.dry.core.outbox.OutboxItem
import jakarta.persistence.Convert

interface VerificationOutboxTask : OutboxItem<VerificationOutboxTask.ID> {
    val type: VerificationType
    val status: VerificationStatus
    val enrolment: PartialEnrolment

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        override fun toString(): String = value.toString()

        companion object {
            val NULL = ID(0)
        }
    }

    enum class VerificationType { DEDUPE, IDENTITY_DOCUMENT, SUBSCRIPTION_PAYER }

    enum class VerificationStatus { PENDING, COMPLETED, FAILED }
}
