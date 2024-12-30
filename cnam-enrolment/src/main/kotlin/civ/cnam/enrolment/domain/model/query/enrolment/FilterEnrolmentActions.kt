package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentActionListItem
import dev.dry.core.data.pagination.Page
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
interface FilterEnrolmentActions {
    operator fun invoke(
        pageNumber: Int,
        pageSize: Int,
        filter: EnrolmentActionFilter
    ): Page<EnrolmentActionListItem>
}