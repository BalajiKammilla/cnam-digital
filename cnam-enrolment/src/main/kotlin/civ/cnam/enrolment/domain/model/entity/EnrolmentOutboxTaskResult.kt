package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface EnrolmentOutboxTaskResult {
    val id: ID
    val enrolment: PartialEnrolment
    val identityDocument: IdentityDocument?
    val supportingDocument: SupportingDocument?
    val pageNumber: Int
    val sequenceNumber: Int
    val processingId: String
    val startedAt: LocalDateTime
    val completedAt: LocalDateTime
    val failureReason: String?
    val failureError: String?

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        override fun toString(): String = value.toString()
    }
}