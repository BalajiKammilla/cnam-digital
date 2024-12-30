package civ.cnam.enrolment.domain.model.type.enrolment.reporting

interface EnrolmentMetrics {
    val startedCount: Long
    val incompleteCount: Long
    val completedCount: Long
    val dedupeMatchCount: Long
    val approvalRequiredCount: Long
    val approvalPendingCount: Long
    val approvalGrantedCount: Long
    val approvalDeclinedCount: Long

    class MutableEnrolmentMetrics(
        override var startedCount: Long = 0,
        override var incompleteCount: Long = 0,
        override var completedCount: Long = 0,
        override var dedupeMatchCount: Long = 0,
        override var approvalRequiredCount: Long = 0,
        override var approvalPendingCount: Long = 0,
        override var approvalGrantedCount: Long = 0,
        override var approvalDeclinedCount: Long = 0,
    ) : EnrolmentMetrics
}
