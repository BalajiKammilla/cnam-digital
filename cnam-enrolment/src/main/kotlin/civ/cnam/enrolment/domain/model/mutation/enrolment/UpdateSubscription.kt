package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.CORRECTIVE_ACTION_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_DETAILS_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_NOT_FOUND_FOR_MOBILE_NUMBER
import civ.cnam.enrolment.domain.error.EnrolmentErrors.INVALID_SUBSCRIPTION_DETAILS_UPDATE
import civ.cnam.enrolment.domain.error.EnrolmentErrors.SERVER_ERROR
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.error.toError
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber

interface UpdateSubscription {
    operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
        subscriptionDetails: SubscriptionDetailsData,
    ): Either<UpdateSubscriptionError, SubscriptionUpdated>

    class SubscriptionUpdated(val enrolmentId: PartialEnrolment.ID)

    sealed class UpdateSubscriptionError(error: CodedError) : CodedError.DefaultCodedError(error) {
        object InvalidSubscriptionDetailsUpdate : UpdateSubscriptionError(INVALID_SUBSCRIPTION_DETAILS_UPDATE)
        object EnrolmentNotFound : UpdateSubscriptionError(ENROLMENT_NOT_FOUND)
        object EnrolmentNotFoundForMobileNumber : UpdateSubscriptionError(ENROLMENT_NOT_FOUND_FOR_MOBILE_NUMBER)
        object EnrolmentDetailsNotFound : UpdateSubscriptionError(ENROLMENT_DETAILS_NOT_FOUND)
        object CorrectiveActionNotFound : UpdateSubscriptionError(CORRECTIVE_ACTION_NOT_FOUND)
        object UpdateSubscriptionFailed : UpdateSubscriptionError(SERVER_ERROR.toError())
    }
}