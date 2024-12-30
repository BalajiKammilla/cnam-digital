package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.EnrolmentAction
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import dev.dry.core.tracing.TraceId
import java.time.LocalDateTime

class EnrolmentActionListItem(
    val id: Long?,
    val kind: EnrolmentActionKind?,
    val createdAt: LocalDateTime?,
    val traceId: TraceId?,
){
    constructor(action: EnrolmentAction): this(
        id = action.id,
        kind = action.kind,
        createdAt = action.createdAt,
        traceId = action.traceId
    )
}
