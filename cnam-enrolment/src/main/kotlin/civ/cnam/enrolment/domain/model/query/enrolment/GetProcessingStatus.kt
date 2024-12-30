package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_PROCESSING_STATUS_NOT_FOUND
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

interface GetProcessingStatus {
    operator fun invoke(externalId: EnrolmentId): Either<GetProcessingStatusError, ProcessingStatus>

    sealed class GetProcessingStatusError(error: CodedError) : CodedError.DefaultCodedError(error)
    object EnrolmentProcessingStatusNotFound : GetProcessingStatusError(ENROLMENT_PROCESSING_STATUS_NOT_FOUND)

    class ProcessingStatus(
        val production: String?,
        val collectionSite: String?,
        val delivery: String?
    )
}