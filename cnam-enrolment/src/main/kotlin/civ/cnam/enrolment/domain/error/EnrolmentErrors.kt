package civ.cnam.enrolment.domain.error

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.error.CodedError.DefaultCodedError
import dev.dry.common.error.ErrorCodeCollection
import dev.dry.common.error.toError
import dev.dry.common.text.message.DynamicMessage
import dev.dry.common.text.message.MapParameterValueResolver.Companion.parameters

object EnrolmentErrors : ErrorCodeCollection(52, "enrolment") {
    sealed class EnrolmentError(error: CodedError) : DefaultCodedError(error)

    private val INVALID_REVIEW_TASK_UPDATE_TYPE = badRequest.get(
        1, "invalid update type")
    object InvalidReviewTaskUpdateType : EnrolmentError(INVALID_REVIEW_TASK_UPDATE_TYPE.toError()),
        UpdateReviewTaskError.InvalidReviewTaskUpdateType

    private val INVALID_REVIEW_TASK_UPDATE_STATUS = badRequest.get(
        2, "invalid update status")
    object InvalidReviewTaskUpdateStatus : EnrolmentError(INVALID_REVIEW_TASK_UPDATE_STATUS.toError()),
        UpdateReviewTaskError.InvalidReviewTaskUpdateStatus

    val INVALID_SUBSCRIPTION_DETAILS_UPDATE = badRequest.get(
        3,
        "invalid subscription details update -- subscription and/or payer proof of identity are required"
    ).toError()

    // ========================================================================
    // ---{ NOT FOUND }---
    // ========================================================================

    val REFERENCE_VALUE_NOT_FOUND_WITH_CODE = notFound.get(
        0,
        "reference data value not found with code",
        DynamicMessage { "'${it["type"]}' not found with code '${it["code"]}'" }
    )

    private val ENROLMENT_NOT_FOUND_WITH_ID = notFound.get(
        1,
        "enrolment not found with id",
        DynamicMessage { "enrolment not found with id '${it["id"]}'" }
    )
    fun enrolmentNotFound(enrolmentId: EnrolmentId?): CodedError {
        return ENROLMENT_NOT_FOUND_WITH_ID.toError(parameters("id" to (enrolmentId?.value ?: "")))
    }

    val DEDUPE_NOT_FOUND_FOR_ENROLMENT = notFound.get(
        3, "dedupe not found for enrolment").toError()

    val DEDUPE_NOT_FOUND = notFound.get(
        4, "dedupe not found").toError()

    val ENROLMENT_NOT_FOUND = notFound.get(
        5, "enrolment not found").toError()
    val ENROLMENT_NOT_FOUND_FOR_MOBILE_NUMBER = notFound.get(
        6, "enrolment not found").toError()
    sealed interface EnrolmentNotFoundError : CodedError, CompleteEnrolmentError.EnrolmentNotFoundError
    object EnrolmentNotFound : EnrolmentError(ENROLMENT_NOT_FOUND),
        EnrolmentNotFoundError,
        CompleteEnrolmentError.EnrolmentNotCompletedError,
        UpdateReviewTaskError.EnrolmentNotFound,
        CreateEnrolmentOutboxTaskError.EnrolmentNotFound
    object EnrolmentNotFoundForMobileNumber : EnrolmentError(ENROLMENT_NOT_FOUND_FOR_MOBILE_NUMBER),
        EnrolmentNotFoundError,
        CompleteEnrolmentError.EnrolmentNotFoundForMobileNumber

    val ENROLMENT_PROCESSING_STATUS_NOT_FOUND = notFound.get(
        7, "enrolment processing status not found" ).toError()

    val ENROLMENT_OUTBOX_TASK_NOT_FOUND = notFound.get(
        8, "enrolment outbox task not found").toError()

    val VERIFICATION_OUTBOX_TASK_NOT_FOUND = notFound.get(
        9, "verification outbox task not found").toError()
    object VerificationOutboxTaskNotFound : EnrolmentError(VERIFICATION_OUTBOX_TASK_NOT_FOUND)

    val CORRECTIVE_ACTION_NOT_FOUND = notFound.get(
        10, "corrective action not found").toError()
    object CorrectiveActionNotFound : EnrolmentError(CORRECTIVE_ACTION_NOT_FOUND)

    private val REVIEW_TASK_NOT_FOUND = notFound.get(
        11, "review task not found").toError()
    object ReviewTaskNotFound : EnrolmentError(REVIEW_TASK_NOT_FOUND),
        UpdateReviewTaskError.ReviewTaskNotFound,
        GetReviewTaskDetailsError.ReviewTaskNotFound

    val ENROLMENT_USER_NOT_FOUND = notFound.get(
        12, "enrolment user not found").toError()
    object EnrolmentUserNotFound : EnrolmentError(ENROLMENT_USER_NOT_FOUND),
        UpdateReviewTaskError.UserNotFound

    val IDENTITY_DOCUMENT_NOT_FOUND = notFound.get(
        13, "identity document not found").toError()

    val ENROLMENT_DETAILS_NOT_FOUND = notFound.get(
        14, "enrolment details not found").toError()
    object EnrolmentDetailsNotFound : EnrolmentError(ENROLMENT_DETAILS_NOT_FOUND),
        UpdateReviewTaskError.EnrolmentDetailsNotFound

    // ========================================================================
    // ---{ CONFLICT }---
    // ========================================================================

    val ENROLMENT_ALREADY_COMPLETED = conflict.get(
        0, "enrolment already completed").toError()

    private val ENROLMENT_STATUS_NOT_SET_TO_COMPLETED = conflict.get(
        1,
        "enrolment status not updated to completed"
    ).toError()
    object EnrolmentStatusNotSetToCompleted : EnrolmentError(ENROLMENT_STATUS_NOT_SET_TO_COMPLETED),
        CompleteEnrolmentError.EnrolmentStatusNotSetToCompleted

    val ENROLMENT_INCOMPLETE = conflict.get(2, "enrolment incomplete").toError()
    object EnrolmentIncomplete : EnrolmentError(ENROLMENT_INCOMPLETE),
        CreateEnrolmentOutboxTaskError.EnrolmentNotFound

    private val ENROLMENT_DETAILS_MISSING = conflict.get(
        3,
        "enrolment details missing",
        DynamicMessage {
            "enrolment details missing - the following details are pending submission: '${it["missingDetails"]}'"
        }
    )
    class EnrolmentMissingDetails(missingDetails: Set<String>) : EnrolmentError(
        ENROLMENT_DETAILS_MISSING.toError(parameters(
            "missingDetails" to missingDetails.joinToString()
        ))
    ), CompleteEnrolmentError.EnrolmentDetailsMissing

    val ENROLMENT_APPROVAL_NOT_REQUIRED = conflict.get(
        4,
        "enrolment approval not required",
    )

    val ENROLMENT_APPROVAL_STATUS_ALREADY_UPDATED = conflict.get(
        5,
        "enrolment approval status already updated",
    )

    val CANNOT_UPDATE_COMPLETED_ENROLMENT = conflict.get(
        7, "cannot update completed enrolment").toError()
    object CannotUpdateCompletedEnrolment : EnrolmentError(CANNOT_UPDATE_COMPLETED_ENROLMENT)

    private val REVIEW_TASK_ALREADY_COMPLETED = conflict.get(
        8, "review task already completed").toError()
    object ReviewTaskAlreadyCompleted : EnrolmentError(REVIEW_TASK_ALREADY_COMPLETED),
        UpdateReviewTaskError.ReviewTaskAlreadyCompleted


    // ========================================================================
    // ---{ SERVER }---
    // ========================================================================

    val SERVER_ERROR = server.get(
        0,
        "server error",
        "something went wrong"
    )

    val ATTACHMENT_RETRIEVAL_FAILED = server.get(
        1, "attachment retrieval failed").toError()

    val INVALID_PAGE_NUMBER = server.get(
        2, "invalid page number").toError()

    private val ENROLMENT_VERIFIER_NOT_FOUND = server.get(
        3,
        "enrolment verifier not found",
        DynamicMessage { "enrolment verifier not found for verification type '${it["type"]}'" }
    )
    fun enrolmentVerifierNotFound(type: VerificationOutboxTask.VerificationType): CodedError {
        return ENROLMENT_VERIFIER_NOT_FOUND.toError(parameters(
            "type" to type.name
        ))
    }
    private val ENROLMENT_VERIFIER_VERIFICATION_FAILED = server.get(
        4,
        description = "enrolment verifier verification failed",
        errorMessage = "enrolment verifier verification failed",
    )
    fun enrolmentVerifierVerificationFailed(cause: Exception): CodedError {
        return ENROLMENT_VERIFIER_VERIFICATION_FAILED.toError(cause)
    }

    val FAILED_TO_CREATE_ALERT = server.get(
        5,
        description = "failed to create alert",
        errorMessage = "failed to create alert",
    ).toError()
    object FailedToCreateAlert : EnrolmentError(FAILED_TO_CREATE_ALERT),
        UpdateReviewTaskError.FailedToCreateAlert

    val FAILED_TO_CREATE_CORRECTIVE_ACTION = server.get(
        6,
        description = "failed to create corrective action",
        errorMessage = "failed to create corrective action",
    ).toError()
    object FailedToCreateCorrectiveAction : EnrolmentError(FAILED_TO_CREATE_CORRECTIVE_ACTION),
        UpdateReviewTaskError.FailedToCreateCorrectiveAction

    private val FAILED_TO_LOAD_DOCUMENT_ATTACHMENT_ID = server.get(
        7,
        description = "failed to load identity document attachment"
    ).toError()
    object FailedToLoadDocumentAttachment : EnrolmentError(FAILED_TO_LOAD_DOCUMENT_ATTACHMENT_ID),
        GetReviewTaskDetailsError.FailedToLoadDocumentAttachment

    val UPDATE_ENROLMENT_TO_VERIFIED_FAILED = server.get(
        8, description = "failed to update enrolment to verified").toError()
    val UPDATE_ENROLMENT_CORRECTIVE_ACTION_PENDING_COUNT_FAILED = server.get(
        9, description = "failed to update enrolment to verified corrective action pending count").toError()
    val GET_ENROLMENT_STATUS_FAILED = server.get(
        10, "failed to retrieve enrolment status").toError()
    val GET_ENROLMENT_PROCESSING_STATUS_FAILED = server.get(
        11, "failed to get enrolment processing status").toError()
}
