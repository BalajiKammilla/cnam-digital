package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface CorrectiveAction {
    val id: ID
    val type: CorrectiveActionType
    val status: CorrectiveActionStatus
    val createdAt: LocalDateTime
    val completedAt: LocalDateTime?

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        companion object {
            val NULL = ID(0)
        }
    }

    enum class CorrectiveActionType { IDENTITY_DOCUMENT, SUBSCRIPTION_PAYER }

    enum class CorrectiveActionStatus { PENDING, COMPLETED }
}