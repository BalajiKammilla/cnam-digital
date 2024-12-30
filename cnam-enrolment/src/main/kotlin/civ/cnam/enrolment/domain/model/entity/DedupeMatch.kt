package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import java.time.LocalDate

interface DedupeMatch {
    val id: ID
    val firstName: FirstName
    val lastName: LastName
    val dateOfBirth: LocalDate
    val identityDocumentTypeCode: DocumentTypeCode
    val identityDocumentNumber: DocumentNumber

    @JvmInline
    value class ID(val value: Long) {
        companion object {
            val NULL = ID(0)
        }
    }
}
