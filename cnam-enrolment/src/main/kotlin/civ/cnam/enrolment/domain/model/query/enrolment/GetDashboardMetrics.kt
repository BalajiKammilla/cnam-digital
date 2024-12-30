package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.type.enrolment.reporting.DashboardMetrics
import civ.cnam.enrolment.domain.service.EnrolmentMetricsCollector
import dev.dry.audit.domain.service.AuditEventMetricsCollector
import dev.dry.user.domain.service.UserMetricsCollector
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetDashboardMetrics(
    private val enrolmentMetricsCollector: EnrolmentMetricsCollector,
    private val userMetricsCollector: UserMetricsCollector,
    private val auditEventMetricsCollector: AuditEventMetricsCollector,
) {
    operator fun invoke(fromDate: LocalDate?, toDate: LocalDate?): DashboardMetrics {
        val enrolmentCounters = enrolmentMetricsCollector.collect(
            fromDate = fromDate,
            toDate = toDate
        )
        val frontOfficeUserMetrics = userMetricsCollector.collect(
            fromDate = fromDate,
            toDate = toDate
        )
        val eventMetrics = auditEventMetricsCollector.collect(
            fromDate = fromDate,
            toDate = toDate
        )
        return DashboardMetrics(
            enrolmentCounters,
            frontOfficeUserMetrics,
            eventMetrics,
        )
    }
}
