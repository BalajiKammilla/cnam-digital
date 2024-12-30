package civ.cnam.zetes.api.model

import civ.cnam.zetes.api.client.model.ZetesEnrolment
import civ.cnam.zetes.api.client.model.ZetesIdentificationDocument
import dev.dry.core.data.format.xml.Xml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import java.time.LocalDate

class ZetesEnrolmentTest : BaseXmlTest() {
    companion object {
        const val DOCUMENT_NUMBER = "444-444-444"
        const val DOCUMENT_TYPE_CODE = "CNI"
        // Personal Information
        const val TITLE_CODE = "M"
        const val FIRST_NAMES = "Aya"
        const val LAST_NAME = "Aya"
        const val MAIDEN_NAME = "Aya"
        const val GENDER = "Aya"
        const val NATIONALITY_CODE = "CIV"
        const val NATIONALITY_LABEL = "Aya"
        const val EMAIL = "Aya"
        const val PHONE_NUMBER = "Aya"
        const val PHONE_TYPE = "mobile"
        const val POST_OFFICE_BOX = "123123"
        val DATE_OF_BIRTH = LocalDate.of(2000, 1, 1)
        const val BIRTH_CERTIFICATE_NUMBER = "222222"
        val BIRTH_CERTIFICATE_ISSUE_DATE = LocalDate.of(2000, 1, 15)
        const val BIRTH_COUNTRY_CODE = "CIV"
        const val BIRTH_COUNTRY_LABEL = "Aya"
        const val BIRTH_SUB_PREFECTURE_CODE = "Aya"
        const val BIRTH_SUB_PREFECTURE_LABEL = "Aya"
        const val FAMILY_STATUS_CODE = "Aya"
        const val PERSON_TYPE_CODE = "Aya"
        const val PERSON_TYPE_LABEL = "Aya"

        const val PROFESSION_CODE = "Aya"
        const val PROFESSION_LABEL = "Aya"

        // Attachments
        const val PHOTO = "PHOTO"
        const val SIGNATURE = "SIGNATURE"

        // Subscription
        const val PAID_BY_CODE = "PAID_BY"
        const val PAID_BY_FIRST_NAME = "PAID_BY"
        const val PAID_BY_LAST_NAME = "PAID_BY"
        const val PAID_BY_ENROLMENT_ID = "PAID_BY"
    }

    @Test
    fun test() {
        val enrolmentXml = ZetesEnrolment.newInstance()
            .identificationDocument {
                documentNumber(DOCUMENT_NUMBER)
                documentType(DOCUMENT_TYPE_CODE)
            }
            .personalInformation {
                title(TITLE_CODE)
                firstNames(FIRST_NAMES)
                lastName(LAST_NAME)
                maidenName(MAIDEN_NAME)
                gender(GENDER)
                currentNationality {
                    iso31661a3(NATIONALITY_CODE)
                    label(NATIONALITY_LABEL)
                }
                email(EMAIL)
                phone {
                    number(PHONE_NUMBER)
                    type(PHONE_TYPE)
                }
                postOfficeBox(POST_OFFICE_BOX)
            }
            .birth {
                date(DATE_OF_BIRTH)
                extractNumber(BIRTH_CERTIFICATE_NUMBER)
                extractIssuingDate(BIRTH_CERTIFICATE_ISSUE_DATE)
                location {
                    country {
                        iso31661a3(BIRTH_COUNTRY_CODE)
                        label(BIRTH_COUNTRY_LABEL)
                    }
                    subPrefecture(BIRTH_SUB_PREFECTURE_CODE)
                    locationCode(BIRTH_SUB_PREFECTURE_CODE)
                }
            }
            .familyStatus(FAMILY_STATUS_CODE)
            .personTypeCode(PERSON_TYPE_CODE)
            .personType(PERSON_TYPE_LABEL)
            .professionalInformation {
                professionCode(PROFESSION_CODE)
                profession(PROFESSION_LABEL)
            }
            .subscription {
                paidBy(PAID_BY_CODE)
                naturalPerson {
                    firstName(PAID_BY_FIRST_NAME)
                    lastname(PAID_BY_LAST_NAME)
                    enrolmentId(PAID_BY_ENROLMENT_ID)
                }
            }
            .fingerprint(1, "F1")
            .fingerprint(2, "F2")
            .fingerprint(3, "F3")
            .fingerprint(4, "F4")
            .fingerprint(5, "F5")
            .fingerprint(6, "F6")
            .fingerprint(7, "F7")
            .fingerprint(8, "F8")
            .fingerprint(9, "F9")
            .fingerprint(10, "F10")
            .photo(PHOTO)
            .signature(SIGNATURE)
            .numberOfScannedDocuments(3)
            .toXmlString()

        val rootElement = Xml.Transform.toDocument(enrolmentXml).documentElement

        assertEquals(PERSON_TYPE_CODE, childText(rootElement, ZetesEnrolment.PERSON_TYPE_CODE))
        assertEquals(PERSON_TYPE_LABEL, childText(rootElement, ZetesEnrolment.PERSON_TYPE))

        val request = child(rootElement, ZetesEnrolment.REQUEST)

        val personalInformation = child(rootElement, ZetesEnrolment.PERSONAL_INFORMATION)

        val birth = child(rootElement, ZetesEnrolment.BIRTH)

        val familyStatus = child(rootElement, ZetesEnrolment.FAMILY_STATUS)
        assertEquals(FAMILY_STATUS_CODE, childText(familyStatus, ZetesEnrolment.FAMILY_STATUS_STATUS))

        val address = child(rootElement, ZetesEnrolment.ADDRESS)

        val professionalInformation = child(rootElement, ZetesEnrolment.PROFESSIONAL_INFORMATION)

        val subscription = child(rootElement, ZetesEnrolment.SUBSCRIPTION)

        val fingerprints = child(rootElement, ZetesEnrolment.FINGERPRINTS)
            .getElementsByTagName(ZetesEnrolment.FINGERPRINT)
        assertEquals(10, fingerprints.length)
        (1..10).forEach { position ->
            val fingerprint = fingerprints.item(position - 1) as Element
            assertEquals("$position", childText(fingerprint, ZetesEnrolment.FINGERPRINT_POSITION))
            assertEquals("F$position", childText(fingerprint, ZetesEnrolment.FINGERPRINT_CAPTURE))
        }

        val photo = child(rootElement, ZetesEnrolment.PHOTO)
        assertEquals(PHOTO, childText(photo, ZetesEnrolment.PHOTO_CAPTURE))

        val signature = child(rootElement, ZetesEnrolment.SIGNATURE)
        assertEquals(SIGNATURE, childText(signature, ZetesEnrolment.SIGNATURE_CAPTURE))

        val identificationDocument = child(rootElement, ZetesEnrolment.IDENTIFICATION_DOCUMENT)
        assertEquals(DOCUMENT_TYPE_CODE, childText(identificationDocument, ZetesIdentificationDocument.DOCUMENT_TYPE))
        assertEquals(DOCUMENT_NUMBER, childText(identificationDocument, ZetesIdentificationDocument.DOCUMENT_NUMBER))
    }
}
