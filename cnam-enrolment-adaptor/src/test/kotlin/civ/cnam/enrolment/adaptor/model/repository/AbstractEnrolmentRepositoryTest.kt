package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.partial.AddressData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.BirthDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ContactDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.PersonalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ProfessionalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import civ.cnam.enrolment.domain.model.value.document.BirthCertificateNumber
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import dev.dry.core.data.model.value.MobileNumber
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

abstract class AbstractEnrolmentRepositoryTest {
    abstract val unit: EnrolmentRepository

    @Test
    @Transactional
    fun createPartialEnrolment() {
        val enrolment = unit.createPartialEnrolment(
            mobileNumber = MOBILE_NUMBER,
            fingerprintsAttachmentId = null,
            photoAttachmentId = null,
            signatureAttachmentId = null,
            enrolmentDetails = null,
        )

        assertNotEquals(PartialEnrolment.ID.NULL, enrolment.id)
        assertNull(enrolment.completedAt)
        assertFalse(enrolment.completed)
        assertNull(enrolment.processedAt)
        assertFalse(enrolment.processed)
        assertNull(enrolment.approvalStatus)
        assertNull(enrolment.dedupeMatch)
        assertEquals(MOBILE_NUMBER, enrolment.mobileNumber)
        assertNotNull(enrolment.identityDocument)
        assertEquals(DOCUMENT_NUMBER, enrolment.identityDocument?.documentNumber)
        assertEquals(PASSPORT_DOCUMENT_TYPE, enrolment.identityDocument?.documentTypeCode)
        assertNull(enrolment.fingerprintsAttachmentId)
        assertNull(enrolment.photoAttachmentId)
        assertNull(enrolment.signatureAttachmentId)
        assertNull(enrolment.enrolmentDetails)
        assertNull(enrolment.requiredSupportingDocuments)
    }

    @Test
    @Transactional
    fun createPartialEnrolmentWithEnrolmentDetails() {
        val enrolmentDetails = enrolmentDetails()
        val enrolment = unit.createPartialEnrolment(
            mobileNumber = MOBILE_NUMBER,
            fingerprintsAttachmentId = null,
            photoAttachmentId = null,
            signatureAttachmentId = null,
            enrolmentDetails = enrolmentDetails,
        )

        assertNotNull(enrolment.enrolmentDetails)
    }

    @Test
    @Transactional
    fun updateCompletedAt() {
        val enrolment = unit.createPartialEnrolment(
            mobileNumber = MOBILE_NUMBER,
            fingerprintsAttachmentId = fingerprintsAttachmentId(),
            photoAttachmentId = photoAttachmentId(),
            signatureAttachmentId = signatureAttachmentId(),
            enrolmentDetails = enrolmentDetails(),
        )

        val completedEnrolmentsBefore = unit.findCompletedEnrolments(MOBILE_NUMBER).size
        val enrolmentsPendingProcessingBefore = unit.findEnrolmentsPendingProcessing(pageNumber = 1, pageSize = 10).totalSize

        unit.updateCompletedAt(enrolment.enrolmentId, LocalDateTime.now())

        val completedEnrolmentsAfter = unit.findCompletedEnrolments(MOBILE_NUMBER).size
        val enrolmentsPendingProcessingAfter = unit.findEnrolmentsPendingProcessing(pageNumber = 1, pageSize = 10).totalSize

        assertEquals(completedEnrolmentsAfter - completedEnrolmentsBefore, 1)
        assertEquals(enrolmentsPendingProcessingAfter - enrolmentsPendingProcessingBefore, 1)
    }

    companion object {
        val MOBILE_NUMBER = MobileNumber("+25555123123")
        val DOCUMENT_NUMBER = DocumentNumber("123456789")
        val PASSPORT_DOCUMENT_TYPE = DocumentTypeCode("PAS")

        fun fingerprintsAttachmentId(): FingerprintsAttachmentId = FingerprintsAttachmentId(UUID.randomUUID().toString())
        fun photoAttachmentId(): PhotoAttachmentId = PhotoAttachmentId(UUID.randomUUID().toString())
        fun signatureAttachmentId(): SignatureAttachmentId = SignatureAttachmentId(UUID.randomUUID().toString())

        fun enrolmentDetails(): EnrolmentDetailsData {
            val personalDetails = PersonalDetailsData(
                titleCode = TitleCode("MR"),
                firstNames = FirstName("First"),
                lastName = LastName("Last"),
                currentNationalityCode = IsoAlpha3CountryCode("CIV"),
                maritalStatusCode = MaritalStatusCode("MARRIED"),
                maidenName = null,
            )
            val birthDetails = BirthDetailsData(
                date = LocalDate.MIN,
                certificateNumber = BirthCertificateNumber(""),
                certificateIssueDate = LocalDate.MIN,
                countryCode = IsoAlpha3CountryCode("CIV"),
                subPrefectureCode = null
            )
            val address = AddressData(
                subPrefectureCode = SubPrefectureCode(""),
                agencyCode = AgencyCode("")
            )
            val contactDetails = ContactDetailsData()
            val professionalDetails = ProfessionalDetailsData()
            val subscriptionDetails = SubscriptionDetailsData()
            return EnrolmentDetailsData(
                personalDetails = personalDetails,
                birthDetails = birthDetails,
                address = address,
                contactDetails = contactDetails,
                professionalDetails = professionalDetails,
                subscriptionDetails = subscriptionDetails
            )
        }
    }
}