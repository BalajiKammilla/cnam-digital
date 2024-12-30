package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import dev.dry.core.tracing.TraceId
import java.time.LocalDateTime

interface EnrolmentAction {
    val id: Long
    val kind: EnrolmentActionKind
    val createdAt: LocalDateTime
    val enrolment: PartialEnrolment
    val deviceSession: EnrolmentDeviceSession?
    val attachmentId: String?
    val traceId: TraceId
}