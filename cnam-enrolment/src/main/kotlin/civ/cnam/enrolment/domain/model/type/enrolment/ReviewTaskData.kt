package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType
import civ.cnam.enrolment.domain.model.type.attachment.IdentityDocumentAttachment
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.BirthDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.PersonalDetails
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import java.time.LocalDateTime

sealed interface ReviewTaskData {
    val id: ReviewTask.ID
    val type: ReviewTaskType
}

class IdentityDocumentReviewTaskData(
    override val id: ReviewTask.ID,
    val personalDetails: PersonalDetails,
    val birthDetails: BirthDetails,
    val applicantPhoto: PhotoAttachment,
    val identityDocument: IdentityDocumentAttachment,
) : ReviewTaskData {
    override val type: ReviewTaskType = ReviewTaskType.IDENTITY_DOCUMENT
}

class DedupeReviewTaskData(
    override val id: ReviewTask.ID,
    val enrolment: EnrolmentDedupeReviewTaskData,
    val matchedEnrolments: List<EnrolmentDedupeReviewTaskData>
) : ReviewTaskData {
    override val type: ReviewTaskType = ReviewTaskType.DEDUPE
}

class EnrolmentDedupeReviewTaskData(
    val enrolmentId: EnrolmentId,
    val completedAt: LocalDateTime,
    val personalDetails: PersonalDetails,
    val birthDetails: BirthDetails,
    val applicantPhoto: PhotoAttachment,
    val identityDocument: IdentityDocumentAttachment,
)
