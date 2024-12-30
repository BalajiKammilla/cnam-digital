package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.type.enrolment.reporting.EnrolmentMetrics
import dev.dry.common.time.SqlDateTimeRange
import dev.dry.core.jpa.operations.JpaOperations
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.persistence.Tuple
import java.time.LocalDate

@Singleton
class GetEnrolmentMetrics(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) {
    operator fun invoke(fromDate: LocalDate?, toDate: LocalDate?): EnrolmentMetrics {
        val dateRange = SqlDateTimeRange.from(fromDate, toDate) ?: SqlDateTimeRange.MAX
        val sql = """
            SELECT 'startedCount', COUNT(1) FROM partial_enrolment 
            WHERE started_at BETWEEN :fromDate AND :toDate
            UNION ALL
            SELECT 'incompleteCount', COUNT(1) FROM partial_enrolment 
            WHERE started_at BETWEEN :fromDate AND :toDate
            AND completed_at IS NULL
            UNION ALL
            SELECT 'completedCount', COUNT(1) FROM partial_enrolment
            WHERE started_at BETWEEN :fromDate AND :toDate
            AND completed_at IS NOT NULL
            UNION ALL
            SELECT 'dedupeMatchCount', COUNT(1) FROM partial_enrolment 
            WHERE started_at BETWEEN :fromDate AND :toDate
            AND dedupe_match_id IS NOT NULL
            UNION ALL
            SELECT 'approvalPendingCount', COUNT(1) FROM partial_enrolment
            WHERE started_at BETWEEN :fromDate AND :toDate 
            AND review_task_pending_count > 0
            UNION ALL
            SELECT 'approvalGrantedCount', COUNT(1) FROM partial_enrolment 
            WHERE started_at BETWEEN :fromDate AND :toDate
            AND approval_status = 1
            UNION ALL
            SELECT 'approvalDeclinedCount', COUNT(1) FROM partial_enrolment
            WHERE started_at BETWEEN :fromDate AND :toDate
            AND approval_status = 0
        """.trimIndent()
        val metrics = EnrolmentMetrics.MutableEnrolmentMetrics()
        jpaOperations.nativeQueryList(sql, Tuple::class) {
            parameter("fromDate", dateRange.start)
            parameter("toDate", dateRange.endInclusive)
        }.forEach { tuple ->
            val field = tuple.get(0, String::class.java)
            val value = tuple.get(1, java.lang.Long::class.java).toLong()
            when (field) {
                "startedCount" -> metrics.startedCount = value
                "incompleteCount" -> metrics.incompleteCount = value
                "completedCount" -> metrics.completedCount = value
                "dedupeMatchCount" -> {
                    metrics.dedupeMatchCount = value
                    metrics.approvalRequiredCount = value
                }
                "approvalPendingCount" -> metrics.approvalPendingCount = value
                "approvalGrantedCount" -> metrics.approvalGrantedCount = value
                "approvalDeclinedCount" -> metrics.approvalDeclinedCount = value
                else -> {}
            }
        }
        return metrics
    }
}