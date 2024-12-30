package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.DedupeReviewTaskEntity
import civ.cnam.enrolment.adaptor.model.entity.IdentityDocumentReviewTaskEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors.FailedToLoadDocumentAttachment
import civ.cnam.enrolment.domain.error.GetReviewTaskDetailsError
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.ReviewTask
import civ.cnam.enrolment.domain.model.query.attachment.LoadAttachment
import civ.cnam.enrolment.domain.model.query.enrolment.GetCompletedEnrolment
import civ.cnam.enrolment.domain.model.query.enrolment.GetReviewTaskDetails
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeReviewTaskData
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentDedupeReviewTaskData
import civ.cnam.enrolment.domain.model.type.enrolment.IdentityDocumentReviewTaskData
import civ.cnam.enrolment.domain.model.type.enrolment.ReviewTaskData
import civ.cnam.enrolment.domain.model.value.attachment.IdentityDocumentAttachmentId
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetReviewTaskDetailsJpa(
    private val getReviewTask: GetReviewTaskJpa,
    private val getCompletedEnrolment: GetCompletedEnrolment,
    private val loadAttachment: LoadAttachment,
) : GetReviewTaskDetails {
    @Transactional
    override fun invoke(id: ReviewTask.ID): Either<GetReviewTaskDetailsError, ReviewTaskData> {
        logger.info("retrieving review task '$id'")
        val reviewTask = getReviewTask(id)
            .fold({ return failed(it) }, { it })

        return when(reviewTask) {
            is IdentityDocumentReviewTaskEntity -> getIdentityDocumentReviewTaskData(reviewTask)
            is DedupeReviewTaskEntity -> getDedupeReviewTaskData(reviewTask)
        }
    }

    private fun getIdentityDocumentReviewTaskData(
        reviewTask: IdentityDocumentReviewTaskEntity
    ): Either<GetReviewTaskDetailsError, IdentityDocumentReviewTaskData> {
        logger.info("retrieving IdentityDocumentReviewTask")
        val enrolment = reviewTask.enrolment
        val identityDocument = reviewTask.identityDocument
        val identityDocumentAttachmentId = IdentityDocumentAttachmentId(identityDocument.documentAttachmentId.value)

        val completedEnrolment = getCompletedEnrolment(enrolment)
            .fold({ return failed(it) }, { it })

        val identityDocumentAttachment = loadAttachment(
            enrolment.enrolmentId,
            identityDocumentAttachmentId
        ).fold({ return failed(FailedToLoadDocumentAttachment) }, { it })

        val applicantPhotoAttachment = loadAttachment(
            enrolment.enrolmentId,
            completedEnrolment.photoAttachmentId
        ).fold({ return failed(FailedToLoadDocumentAttachment) }, { it })

        return completed(
            IdentityDocumentReviewTaskData(
                id = reviewTask.id,
                identityDocument = identityDocumentAttachment,
                applicantPhoto = applicantPhotoAttachment,
                personalDetails = completedEnrolment.enrolmentDetails.personalDetails,
                birthDetails = completedEnrolment.enrolmentDetails.birthDetails,
            )
        )
    }

    private fun getDedupeReviewTaskData(
        reviewTask: DedupeReviewTaskEntity
    ): Either<GetReviewTaskDetailsError, DedupeReviewTaskData> {
        logger.info("retrieving DedupeReviewTask")

        val enrolmentTaskData = toEnrolmentDedupeReviewTaskData(reviewTask.enrolment)
            .fold({ return failed(it) }, { it })

        val matchedEnrolments = mutableListOf<EnrolmentDedupeReviewTaskData>()
        for (dedupeMatchEnrolment in reviewTask.dedupeMatch.enrolments) {
            if (dedupeMatchEnrolment.enrolmentId != reviewTask.enrolment.enrolmentId) {
                continue
            }

            val matchedEnrolmentData = toEnrolmentDedupeReviewTaskData(dedupeMatchEnrolment)
                .fold({ return failed(it) }, { it })
            matchedEnrolments.add(matchedEnrolmentData)
        }

        return completed(
            DedupeReviewTaskData(
                id = reviewTask.id,
                enrolment = enrolmentTaskData,
                matchedEnrolments = matchedEnrolments,
            )
        )
    }

    private fun toEnrolmentDedupeReviewTaskData(
        enrolment: PartialEnrolment
    ): Either<GetReviewTaskDetailsError, EnrolmentDedupeReviewTaskData> {
        val completedEnrolment = getCompletedEnrolment(enrolment)
            .fold({ return left(it) }, { it })
        val identityDocument = completedEnrolment.identityDocument
        val identityDocumentAttachmentId = IdentityDocumentAttachmentId(identityDocument.documentAttachmentId.value)
        val identityDocumentAttachment = loadAttachment(
            enrolment.enrolmentId,
            identityDocumentAttachmentId
        ).fold({ return failed(FailedToLoadDocumentAttachment) }, { it })
        val applicantPhotoAttachment = loadAttachment(
            completedEnrolment.enrolmentId,
            completedEnrolment.photoAttachmentId
        ).fold({ return failed(FailedToLoadDocumentAttachment) }, { it })

        val reviewTaskData = EnrolmentDedupeReviewTaskData(
            enrolmentId = completedEnrolment.enrolmentId,
            completedAt = completedEnrolment.completedAt,
            personalDetails = completedEnrolment.enrolmentDetails.personalDetails,
            birthDetails = completedEnrolment.enrolmentDetails.birthDetails,
            applicantPhoto = applicantPhotoAttachment,
            identityDocument = identityDocumentAttachment,
        )
        return right(reviewTaskData)
    }

    companion object {
        const val GET_REVIEW_TASK_JPQL = "SELECT rt FROM ReviewTask rt WHERE rt.id = :id"

        private val logger = LoggerFactory.getLogger(GetReviewTaskDetailsJpa::class.java)

        private fun failed(error: GetReviewTaskDetailsError): Either.Left<GetReviewTaskDetailsError> {
            logger.info("retrieving review task failed -- $error")
            return left(error)
        }

        private fun <T : ReviewTaskData> completed(data: T): Either.Right<T> {
            logger.info("retrieving review task completed")
            return right(data)
        }
    }
}