package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.EnrolmentDetails
import civ.cnam.enrolment.domain.model.entity.SupportingDocument.Purpose.APPLICANT_PROOF_OF_MARRIAGE
import civ.cnam.enrolment.domain.model.entity.SupportingDocument.Purpose.APPLICANT_PROOF_OF_PROFESSION
import civ.cnam.enrolment.domain.model.entity.SupportingDocument.Purpose.PAYER_PROOF_OF_IDENTITY
import civ.cnam.enrolment.domain.model.referencedata.DocumentCategory.PROOF_OF_IDENTITY
import civ.cnam.enrolment.domain.model.referencedata.DocumentCategory.PROOF_OF_MARRIAGE
import civ.cnam.enrolment.domain.model.referencedata.DocumentCategory.PROOF_OF_PROFESSION
import civ.cnam.enrolment.domain.model.repository.referencedata.DocumentTypeRepository
import civ.cnam.enrolment.domain.model.type.enrolment.RequiredSupportingDocument
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class DetermineRequiredSupportingDocuments(private val documentTypeRepository: DocumentTypeRepository) {
    operator fun invoke(enrolmentDetails: EnrolmentDetails): List<RequiredSupportingDocument> {
        val requiredSupportingDocuments = enrolmentDetails.requiredSupportingDocuments
        return if (requiredSupportingDocuments.isNotEmpty()) {
            val documentTypes = documentTypeRepository.findAll()
            requiredSupportingDocuments.map { purpose ->
                RequiredSupportingDocument(
                    purpose,
                    when (purpose) {
                        APPLICANT_PROOF_OF_MARRIAGE -> documentTypes.filter { it.category == PROOF_OF_MARRIAGE }
                        PAYER_PROOF_OF_IDENTITY -> documentTypes.filter { it.category == PROOF_OF_IDENTITY }
                        APPLICANT_PROOF_OF_PROFESSION -> documentTypes.filter { it.category == PROOF_OF_PROFESSION }
                    },
                )
            }
        } else emptyList()
    }
}