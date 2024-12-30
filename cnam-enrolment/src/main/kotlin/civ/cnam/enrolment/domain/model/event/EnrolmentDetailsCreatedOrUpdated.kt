package civ.cnam.enrolment.domain.model.event

import civ.cnam.enrolment.domain.model.type.enrolment.RequiredSupportingDocument
import civ.cnam.enrolment.domain.model.value.EnrolmentId

class EnrolmentDetailsCreatedOrUpdated(
    val enrolmentId: EnrolmentId,
    val supportingDocuments: List<RequiredSupportingDocument>,
)