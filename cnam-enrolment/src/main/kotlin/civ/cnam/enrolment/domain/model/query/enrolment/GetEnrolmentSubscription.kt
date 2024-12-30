package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFoundError
import civ.cnam.enrolment.domain.model.entity.SubscriptionDetails
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.data.model.value.MobileNumber
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetEnrolmentSubscription(private val enrolmentRepository: EnrolmentRepository) {
    @Transactional
    operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber
    ): Either<EnrolmentNotFoundError, EnrolmentSubscription> {
        return enrolmentRepository.findPartialEnrolment(enrolmentId, mobileNumber)
            .map { enrolment ->
                val subscriptionDetails = enrolment.enrolmentDetails?.subscriptionDetails
                EnrolmentSubscription(subscriptionDetails)
            }
    }

    class EnrolmentSubscription(val subscriptionDetails: SubscriptionDetails?)
}
