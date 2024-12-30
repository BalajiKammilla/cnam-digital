package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentActionEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.query.enrolment.FilterEnrolmentActions
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionListItem
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import dev.dry.common.time.SqlDateRange
import dev.dry.core.data.pagination.Page
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FilterEnrolmentActionsJpa (
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
): FilterEnrolmentActions {
    override operator fun invoke(
        pageNumber: Int,
        pageSize: Int,
        filter: EnrolmentActionFilter
    ): Page<EnrolmentActionListItem> {
        return jpaOperations.queryPage(
            pageNumber = pageNumber,
            pageSize = pageSize,
            entityClass = EnrolmentActionEntity::class
        ) {
            val enrolmentJoin = join<PartialEnrolmentEntity>("enrolment")
            where {
                if (filter.fromCreatedAt != null || filter.toCreatedAt != null) {
                    SqlDateRange.from(filter.fromCreatedAt, filter.toCreatedAt)
                        ?.also { attribute<LocalDate>("createdAt") between it }
                }
                filter.kind?.also { attribute<EnrolmentActionKind>("kind") equal it }
                filter.mobileNumber?.also { attribute<String>("mobileNumber") equal it.value }
                filter.enrolmentId?.also { enrolmentJoin.get<String>("enrolmentId") equal it.value }
            }
        }.mapContent(::EnrolmentActionListItem)
    }
}