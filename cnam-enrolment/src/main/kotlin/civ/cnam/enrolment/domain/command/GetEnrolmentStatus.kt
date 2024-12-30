package civ.cnam.enrolment.domain.command

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.CompleteEnrolmentError
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.ENROLMENT_NOT_FOUND
import civ.cnam.enrolment.domain.error.EnrolmentErrors.GET_ENROLMENT_PROCESSING_STATUS_FAILED
import civ.cnam.enrolment.domain.model.query.enrolment.GetCompletedEnrolment
import civ.cnam.enrolment.domain.model.query.enrolment.GetProcessingStatus
import civ.cnam.enrolment.domain.model.referencedata.DocumentType
import civ.cnam.enrolment.domain.model.repository.referencedata.DocumentTypeRepository
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetEnrolmentStatus(
    private val getCompletedEnrolment: GetCompletedEnrolment,
    private val getProcessingStatus: GetProcessingStatus,
    private val documentTypeRepository: DocumentTypeRepository,
) {
    @Transactional
    operator fun invoke(enrolmentId: EnrolmentId): Either<GetEnrolmentStatusError, EnrolmentStatus> {
        logger.info("retrieving status for enrolment '$enrolmentId'")
        val enrolment = getCompletedEnrolment(enrolmentId)
            .fold({ return error(it) }, { it })
        logger.info("retrieving status for enrolment processing")
        val processingStatus = getProcessingStatus(enrolment.enrolmentId)
            .fold({ return error(it) }, { it })
        return constructEnrolmentStatus(enrolment, processingStatus)
    }

    private fun constructEnrolmentStatus(
        enrolment: CompletedEnrolment,
        processingStatus: GetProcessingStatus.ProcessingStatus,
    ): Either<GetEnrolmentStatusError, EnrolmentStatus> {
        val documentTypeCode = enrolment.identityDocument.documentTypeCode
        val documentType = documentTypeRepository.findByCode(documentTypeCode)
            ?: return error(GetEnrolmentStatusFailed, "document type mnot found with code '$documentTypeCode'")
        val enrolmentStatus = EnrolmentStatus(
            identityDocumentNumber = enrolment.identityDocument.documentNumber,
            identityDocumentType = documentType,
            completedAt = enrolment.completedAt,
            verifiedAt = enrolment.verifiedAt,
            submittedAt = enrolment.processingCompletedAt,
            enrolmentSite = enrolment.enrolmentDetails.address.agency.label,
        )
        return right(enrolmentStatus)
    }

    sealed class GetEnrolmentStatusError(error: CodedError): CodedError.DefaultCodedError(error)
    object EnrolmentNotFound : GetEnrolmentStatusError(ENROLMENT_NOT_FOUND)
    object GetEnrolmentStatusFailed : GetEnrolmentStatusError(EnrolmentErrors.GET_ENROLMENT_STATUS_FAILED)
    object GetEnrolmentProcessingStatusFailed : GetEnrolmentStatusError(GET_ENROLMENT_PROCESSING_STATUS_FAILED)

    class EnrolmentStatus(
        val identityDocumentNumber: DocumentNumber,
        val identityDocumentType: DocumentType,
        /** user completed */
        val completedAt: LocalDateTime?,
        /** same as enrolmentCompletedAt if auto-approved */
        val verifiedAt: LocalDateTime?,
        /** submitted to CNAM backend (Zetes) */
        val submittedAt: LocalDateTime?,
        val enrolmentSite: String?,
    )

    companion object {
        private val logger = LoggerFactory.getLogger(GetEnrolmentStatus::class.java)

        private fun error(error: GetEnrolmentStatusError, cause: Any): Either.Left<GetEnrolmentStatusError> {
            logger.info("failed to get enrolment status - $cause")
            return left(error)
        }

        private fun error(error: CompleteEnrolmentError): Either.Left<GetEnrolmentStatusError> {
            return error(
                when(error) {
                    is EnrolmentErrors.EnrolmentNotFound -> EnrolmentNotFound
                    else -> GetEnrolmentStatusFailed
                },
                error
            )
        }

        private fun error(error: GetProcessingStatus.GetProcessingStatusError): Either.Left<GetEnrolmentStatusError> {
            return error(GetEnrolmentProcessingStatusFailed, error)
        }
    }
}
