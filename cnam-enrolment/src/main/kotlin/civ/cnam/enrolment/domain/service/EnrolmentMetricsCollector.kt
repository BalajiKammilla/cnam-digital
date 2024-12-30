package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.domain.model.type.enrolment.reporting.EnrolmentMetrics
import java.time.LocalDate

interface EnrolmentMetricsCollector {
    fun collect(fromDate: LocalDate?, toDate: LocalDate?): EnrolmentMetrics
}