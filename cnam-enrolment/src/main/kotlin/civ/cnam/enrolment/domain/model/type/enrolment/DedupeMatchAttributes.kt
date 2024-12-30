package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import java.time.LocalDate

open class DedupeMatchAttributes(
    val firstName: FirstName,
    val lastName: LastName,
    val dateOfBirth: LocalDate,
    val identityDocumentTypeCode: DocumentTypeCode,
    val identityDocumentNumber: DocumentNumber,
)
