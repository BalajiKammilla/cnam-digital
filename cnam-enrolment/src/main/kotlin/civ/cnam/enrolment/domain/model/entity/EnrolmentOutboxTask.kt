package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.Convert
import java.time.LocalDateTime

interface EnrolmentOutboxTask {
    val id: ID
    val enrolment: PartialEnrolment
    val identityDocument: IdentityDocument?
    val supportingDocument: SupportingDocument?
    val pageNumber: Int
    val sequenceNumber: Int
    val createdAt: LocalDateTime
    val processingId: String?
    val lastProcessedAt: LocalDateTime?
    val lastFailureReason: String?
    val failureCount: Int

    @JvmInline
    @Convert(converter = ValueClassLongAttributeConverter::class)
    value class ID(val value: Long) {
        override fun toString(): String = value.toString()
    }
}
