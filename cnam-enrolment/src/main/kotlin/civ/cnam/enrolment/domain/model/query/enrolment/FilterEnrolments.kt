package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.function.MapPartialToEnrolmentListItem
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import dev.dry.core.data.pagination.Page
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FilterEnrolments(
    private val enrolmentRepository: EnrolmentRepository,
    private val mapPartialToEnrolmentListItem: MapPartialToEnrolmentListItem,
) {
    operator fun invoke(pageNumber: Int, pageSize: Int, filter: EnrolmentFilter): Page<EnrolmentListItem> {
        return enrolmentRepository.findByFilter(filter, pageNumber = pageNumber, pageSize = pageSize)
            .mapContent(mapPartialToEnrolmentListItem::invoke)
    }
}
