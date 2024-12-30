package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.entity.SupportingDocument.Purpose
import civ.cnam.enrolment.domain.model.referencedata.DocumentType

class RequiredSupportingDocument(
    val purpose: Purpose,
    val documentTypeOptions: List<DocumentType>
)