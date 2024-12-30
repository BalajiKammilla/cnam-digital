package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber

interface IdentityDocument {
    val enrolment: PartialEnrolment
    val documentNumber: DocumentNumber
    val documentTypeCode: DocumentTypeCode
    val documentAttachmentId: DocumentAttachmentId
    val pageCount: Int
    val ocrSucceeded: Boolean
}
