package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.CorrectiveActionEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import civ.cnam.enrolment.domain.model.query.enrolment.FilterCorrectiveActions
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter.DateRangeProperty.*
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FilterCorrectiveActionsJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) : FilterCorrectiveActions {
    override operator fun invoke(
        enrolmentId: EnrolmentId,
        status: CorrectiveActionStatus?,
        mobileNumber: MobileNumber,
    ): List<CorrectiveActionEntity> {
        return jpaOperations.queryList(CorrectiveActionEntity::class) {
            val enrolmentJoin = join<PartialEnrolmentEntity>("enrolment")
            where {
                enrolmentJoin.get<String>("enrolmentId") equal enrolmentId.value
                enrolmentJoin.get<String>("mobileNumber") equal mobileNumber.value
                status?.also { attribute<CorrectiveActionStatus>("status") equal it }
            }
        }
    }
}
