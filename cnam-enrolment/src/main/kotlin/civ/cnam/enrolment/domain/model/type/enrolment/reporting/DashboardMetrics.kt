package civ.cnam.enrolment.domain.model.type.enrolment.reporting

import dev.dry.audit.domain.model.type.AuditEventMetrics
import dev.dry.user.domain.model.type.FrontOfficeUserMetrics

class DashboardMetrics(
    val enrolmentMetrics: EnrolmentMetrics,
    val frontOfficeUserMetrics: FrontOfficeUserMetrics,
    val eventMetrics: AuditEventMetrics,
)
