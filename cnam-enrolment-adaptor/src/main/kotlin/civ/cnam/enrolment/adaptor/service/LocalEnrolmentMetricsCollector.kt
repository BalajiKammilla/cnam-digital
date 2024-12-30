package civ.cnam.enrolment.adaptor.service

import civ.cnam.enrolment.adaptor.model.query.GetEnrolmentMetrics
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.type.enrolment.reporting.EnrolmentMetrics
import civ.cnam.enrolment.domain.service.EnrolmentMetricsCollector
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class LocalEnrolmentMetricsCollector(private val getEnrolmentMetrics: GetEnrolmentMetrics) : EnrolmentMetricsCollector {
    override fun collect(fromDate: LocalDate?, toDate: LocalDate?): EnrolmentMetrics {
        return getEnrolmentMetrics(fromDate = fromDate, toDate = toDate)
    }
}
