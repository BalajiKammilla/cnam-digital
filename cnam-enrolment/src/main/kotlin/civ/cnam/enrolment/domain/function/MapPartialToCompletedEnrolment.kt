package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.CompleteEnrolmentError.EnrolmentNotCompletedError
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentMissingDetails
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentStatusNotSetToCompleted
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.time.TimeProvider
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class MapPartialToCompletedEnrolment(
    private val timeProvider: TimeProvider,
    private val mapEnrolmentDetailsInputToEnrolmentDetails: MapEnrolmentDetailsInputToEnrolmentDetails,
) {
    operator fun invoke(partial: PartialEnrolment): Either<CodedError, CompletedEnrolment> {
        return invoke(partial, partial.completedAt)
    }

    operator fun invoke(
        partial: PartialEnrolment,
        completedAt: LocalDateTime?,
    ): Either<EnrolmentNotCompletedError, CompletedEnrolment> {
        logger.info("mapping partial enrolment to completed -- '${partial.enrolmentId}' ('${partial.id}')")
        val identityDocument = partial.identityDocument
        val supportingDocuments = partial.supportingDocuments.map { it }
        val photoAttachmentId = partial.photoAttachmentId
        val fingerprintsAttachmentId = partial.fingerprintsAttachmentId
        val signatureAttachmentId = partial.signatureAttachmentId
        val enrolmentDetails = partial.enrolmentDetails
        val dateOfBirth = enrolmentDetails?.birthDetails?.date
        val isAgeAboveTwelveYears = if (dateOfBirth != null) {
            ChronoUnit.YEARS.between(dateOfBirth, timeProvider.now().toLocalDate()) > 12
        } else false

        logger.info("isAgeAboveTwelveYears = $isAgeAboveTwelveYears -- $dateOfBirth")

        val missingDetails = mutableSetOf<String>()

        identityDocument ?: missingDetails.add("identity document")
        photoAttachmentId ?: missingDetails.add("photo")
        if (fingerprintsAttachmentId == null && isAgeAboveTwelveYears) missingDetails.add("fingerprints")
        if (signatureAttachmentId == null && isAgeAboveTwelveYears) missingDetails.add("signature")
        enrolmentDetails ?: missingDetails.add("enrolment details")

        val requiredSupportingDocuments = partial.requiredSupportingDocuments
        if (!requiredSupportingDocuments.isNullOrEmpty()) {
            val missingSupportingDocuments = requiredSupportingDocuments.minus(
                supportingDocuments.map { it.purpose }.toSet()
            )
            if (missingSupportingDocuments.isNotEmpty()) {
                missingDetails.add("supporting documents $missingSupportingDocuments")
            }
        }

        return if (missingDetails.isNotEmpty()) {
            logger.info("enrolment missing details -- $missingDetails")
            left(EnrolmentMissingDetails(missingDetails))
        } else if (completedAt == null) {
            logger.info("enrolment completedAt not set")
            left(EnrolmentStatusNotSetToCompleted)
        } else {
            right(
                CompletedEnrolment(
                    mobileNumber = partial.mobileNumber,
                    enrolmentId = partial.enrolmentId,
                    startedAt = partial.startedAt,
                    completedAt = completedAt,
                    verifiedAt = partial.verifiedAt,
                    processingCompletedAt = partial.processedAt,
                    approvalStatus = partial.approvalStatus,
                    identityDocument = identityDocument!!,
                    fingerprintsAttachmentId = fingerprintsAttachmentId,
                    photoAttachmentId = photoAttachmentId!!,
                    signatureAttachmentId = signatureAttachmentId,
                    supportingDocuments = supportingDocuments,
                    enrolmentDetails = mapEnrolmentDetailsInputToEnrolmentDetails(enrolmentDetails!!),
                    externalReference = partial.externalReference
                )
            ).also {
                logger.info("mapped partial enrolment to completed")
            }
        }

    }

    companion object {
        private val logger = LoggerFactory.getLogger(MapPartialToCompletedEnrolment::class.java)
    }
}
