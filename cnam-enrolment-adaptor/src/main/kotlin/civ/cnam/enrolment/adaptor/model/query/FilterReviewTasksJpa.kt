package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.ReviewTaskEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskStatus
import civ.cnam.enrolment.domain.model.query.enrolment.FilterReviewTasks
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter.DateRangeProperty.*
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskFilter
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskFilter.DateRangeProperty
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskListItem
import dev.dry.core.data.pagination.Page
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FilterReviewTasksJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) : FilterReviewTasks {
    override operator fun invoke(pageNumber: Int, pageSize: Int, filter: ReviewTaskFilter): Page<ReviewTaskListItem> {
        return jpaOperations.queryPage(ReviewTaskEntity::class, pageNumber = pageNumber, pageSize = pageSize) {
            val enrolmentJoin = join<ReviewTaskEntity>("enrolment")

            val dateProperty = when(filter.dateRangeProperty ?: DateRangeProperty.COMPLETED_AT) {
                DateRangeProperty.COMPLETED_AT -> attribute<LocalDateTime>("completedAt")
                DateRangeProperty.CREATED_AT -> attribute("startedAt")
            }

            val status = attribute<ReviewTaskStatus>("status")
            where {
                filter.dateRange?.also { dateProperty between it }
                filter.status?.also { status equal it }
                filter.enrolmentId?.also { enrolmentJoin.get<String>("enrolmentId") equal it.value }
            }
        }.mapContent(::ReviewTaskListItem)
    }
}
