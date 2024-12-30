package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.SubscriptionUpdated
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError.EnrolmentDetailsNotFound
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.common.function.flatMap
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateSubscriptionJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
) : UpdateSubscription {
    override operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
        subscriptionDetails: SubscriptionDetailsData,
    ): Either<UpdateSubscriptionError, SubscriptionUpdated> {
        return findPartialEnrolment(
            enrolmentId,
            mobileNumber,
            { UpdateSubscriptionError.EnrolmentNotFound },
            { _, _ -> UpdateSubscriptionError.EnrolmentNotFoundForMobileNumber }
        ).flatMap { enrolment ->
            val enrolmentDetails = enrolment.enrolmentDetails
            if (enrolmentDetails == null) {
                Either.left(EnrolmentDetailsNotFound)
            } else {
                enrolmentDetails.subscriptionDetails = subscriptionDetails
                jpaOperations.persist(enrolmentDetails)
                Either.right(SubscriptionUpdated(enrolment.id))
            }
        }
    }
}