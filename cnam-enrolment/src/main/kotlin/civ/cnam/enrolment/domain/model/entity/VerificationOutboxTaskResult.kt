package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationStatus
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import dev.dry.core.tracing.TraceId
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface VerificationOutboxTaskResult {
    val id: ID
    val processingId: TraceId?
    val task: VerificationOutboxTask
    val enrolment: PartialEnrolment
    val type: VerificationType
    val status: VerificationStatus
    val processedAt: LocalDateTime
    val failureReason: String?
    val failureError: String?

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        override fun toString(): String = value.toString()

        companion object {
            val NULL = ID(0)
        }
    }
}