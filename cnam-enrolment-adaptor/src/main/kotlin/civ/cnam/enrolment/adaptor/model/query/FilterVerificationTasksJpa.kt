package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.VerificationOutboxTaskEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.query.enrolment.FilterVerificationTasks
import civ.cnam.enrolment.domain.model.type.enrolment.VerificationTaskFilter
import civ.cnam.enrolment.domain.model.type.enrolment.VerificationTaskFilter.DateRangeProperty
import dev.dry.core.data.pagination.Page
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FilterVerificationTasksJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) : FilterVerificationTasks {
    override fun invoke(pageNumber: Int, pageSize: Int, filter: VerificationTaskFilter): Page<VerificationOutboxTask> {
        return jpaOperations.queryPage(
            entityClass = VerificationOutboxTaskEntity::class,
            pageNumber = pageNumber,
            pageSize = pageSize,
        ) {
            val dateProperty = when(filter.dateRangeProperty ?: DateRangeProperty.CREATED_AT) {
                DateRangeProperty.CREATED_AT -> attribute<LocalDateTime>("createdAt")
                DateRangeProperty.LAST_PROCESSED_AT -> attribute("lastProcessedAt")
            }

            where {
                filter.dateRange?.also { dateProperty between it }
                filter.type?.also { attribute<VerificationType>("type") equal it }
                filter.enrolmentId?.also {
                    join<VerificationOutboxTaskEntity>("enrolment")
                        .get<String>("enrolmentId") equal it.value
                }
            }
        }
    }
}