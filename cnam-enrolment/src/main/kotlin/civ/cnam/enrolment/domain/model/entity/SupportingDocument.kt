package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId

interface SupportingDocument {
    val id: ID
    val enrolment: PartialEnrolment
    val documentAttachmentId: DocumentAttachmentId
    val purpose: Purpose
    val documentTypeCode: DocumentTypeCode
    val pageCount: Int

    @JvmInline value class ID(val value: Long) {
        companion object {
            val NULL = ID(0)
        }
    }

    enum class Purpose {
        APPLICANT_PROOF_OF_MARRIAGE,
        PAYER_PROOF_OF_IDENTITY,
        APPLICANT_PROOF_OF_PROFESSION
    }
}
