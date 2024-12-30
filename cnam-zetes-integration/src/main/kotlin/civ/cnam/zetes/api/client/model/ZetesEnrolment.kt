package civ.cnam.zetes.api.client.model

import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.type.attachment.Fingerprint
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Address
import civ.cnam.enrolment.domain.model.type.enrolment.completed.BirthDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.ContactDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.PersonalDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.ProfessionalDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.SubscriptionDetails
import dev.dry.common.io.encoding.Base64
import dev.dry.common.io.resource.ClassPathResource
import dev.dry.core.data.format.xml.Xml
import org.w3c.dom.Document

class ZetesEnrolment(document: Document) : ZetesXmlRootObject<ZetesEnrolment>(document) {
    companion object {
        private val XML_TEMPLATE_RESOURCE = ClassPathResource("/template/enrolment.xml")

        fun newInstance(): ZetesEnrolment = newInstance(XML_TEMPLATE_RESOURCE, ::ZetesEnrolment)

        const val REQUEST = "request"
        const val PERSONAL_INFORMATION = "personalInformation"
        const val BIRTH = "birth"
        const val FAMILY_STATUS = "familyStatus"
        const val FAMILY_STATUS_STATUS = "status"
        const val PERSON_TYPE = "personType"
        const val PERSON_TYPE_CODE = "personTypeCode"
        const val IDENTIFICATION_DOCUMENT = "identificationDocument"
        const val ADDRESS = "address"
        const val PROFESSIONAL_INFORMATION = "professionalInformation"
        const val SUBSCRIPTION = "subscription"
        const val FINGERPRINTS = "fingerprints"
        const val FINGERPRINT = "fingerprint"
        const val FINGERPRINT_POSITION = "position"
        const val FINGERPRINT_CAPTURE = "capture"
        const val PHOTO = "photo"
        const val PHOTO_CAPTURE = "capture"
        const val SIGNATURE = "signature"
        const val SIGNATURE_CAPTURE = "capture"
        const val NUMBER_OF_SCANNED_DOCUMENTS = "numberOfScannedDocuments"
    }

    val request = ZetesRequest(child(REQUEST))
    private val personalInformation = ZetesPersonalInformation(child(PERSONAL_INFORMATION))
    private val birth: ZetesBirth = ZetesBirth(child(BIRTH))
    private val identificationDocument = ZetesIdentificationDocument(child(IDENTIFICATION_DOCUMENT))
    private val address: ZetesAddress = ZetesAddress(child(ADDRESS))
    private val professionalInformation = ZetesProfessionalInformation(child(PROFESSIONAL_INFORMATION))
    private val subscription = ZetesSubscription(child(SUBSCRIPTION))
    private val fingerprints = child(FINGERPRINTS)

    private fun deriveGenderFromTitle(title: Title): String {
        return if (title.code == TitleCode.MISTER) {
            GenderCode.MALE.value
        } else {
            GenderCode.FEMALE.value
        }
    }

    fun request(mutator: ZetesRequest.() -> Unit): ZetesEnrolment = mutate(request, mutator)
    fun personalInformation(mutator: ZetesPersonalInformation.() -> Unit): ZetesEnrolment =
        mutate(personalInformation, mutator)
    fun personalInformation(personalDetails: PersonalDetails, contactDetails: ContactDetails): ZetesEnrolment =
        personalInformation {
            title(personalDetails.title.code.value)
            firstNames(personalDetails.firstNames.value)
            lastName(personalDetails.lastName.value)
            maidenName(personalDetails.maidenName)
            gender(deriveGenderFromTitle(personalDetails.title))
            currentNationality {
                iso31661a3(personalDetails.currentNationality.code.value)
                label(personalDetails.currentNationality.label)
            }
            email(contactDetails.email?.value)
            phone {
                number(contactDetails.mobileNumber.value.replace("+225", ""))
                type("mobile")
            }
            postOfficeBox(null)
        }
    fun birth(mutator: ZetesBirth.() -> Unit): ZetesEnrolment = mutate(birth, mutator)
    fun birth(birthDetails: BirthDetails): ZetesEnrolment =
        birth {
            date(birthDetails.date)
            extractNumber(birthDetails.certificateNumber.value)
            extractIssuingDate(birthDetails.certificateIssueDate)
            location {
                val countryCode = birthDetails.country.code.value
                val countryLabel = birthDetails.country.label
                val subPrefectureCode = birthDetails.subPrefecture?.code?.value
                val subPrefectureLabel = birthDetails.subPrefecture?.label
                country {
                    iso31661a3(countryCode)
                    label(countryLabel)
                }
                subPrefecture(subPrefectureCode ?: countryCode)
                city(subPrefectureLabel ?: countryLabel)
                locationCode(subPrefectureCode ?: countryCode)
            }
        }
    fun familyStatus(status: String): ZetesEnrolment = text(arrayOf(FAMILY_STATUS, FAMILY_STATUS_STATUS), status)
    fun personType(personType: String): ZetesEnrolment = text(PERSON_TYPE, personType)
    fun personTypeCode(personTypeCode: String): ZetesEnrolment = text(PERSON_TYPE_CODE, personTypeCode)
    fun identificationDocument(mutator: ZetesIdentificationDocument.() -> Unit): ZetesEnrolment =
        mutate(identificationDocument, mutator)
    fun identificationDocument(identityDocument: IdentityDocument): ZetesEnrolment =
        identificationDocument {
            documentNumber(identityDocument.documentNumber.value)
            documentType(identityDocument.documentTypeCode.value)
        }
    fun address(mutator: ZetesAddress.() -> Unit): ZetesEnrolment = mutate(address, mutator)
    fun address(address: Address): ZetesEnrolment =
        address {
            val subPrefectureCode = address.subPrefecture.code.value
            val subPrefectureLabel = address.subPrefecture.label
            subPrefecture(subPrefectureLabel)
            city(subPrefectureLabel)
            locationCode(subPrefectureCode)
            street("")
        }
    fun professionalInformation(mutator: ZetesProfessionalInformation.() -> Unit): ZetesEnrolment =
        mutate(professionalInformation, mutator)
    fun professionalInformation(professionalDetails: ProfessionalDetails): ZetesEnrolment =
        professionalInformation {
            professionCode(professionalDetails.profession?.code?.value)
            profession(professionalDetails.profession?.label)
            professionalDetails.employer?.also {
                employerOrganisationCode(it.code.value)
                employerOrganisation(it.label)
            }
            professionalDetails.registrationNumberType?.let {
                when(it.code.value) {
                    RegistrationNumberTypeCode.MCNPS -> cnpsNumber(professionalDetails.registrationNumber)
                    RegistrationNumberTypeCode.MFP -> matriculeFP(professionalDetails.registrationNumber)
                    RegistrationNumberTypeCode.MIL -> matriculeMilitary(professionalDetails.registrationNumber)
                }
            }
        }
    fun subscription(mutator: ZetesSubscription.() -> Unit): ZetesEnrolment = mutate(subscription, mutator)
    fun subscription(subscription: SubscriptionDetails): ZetesEnrolment =
        subscription {
            paidBy(subscription.paidBy.code.value)
            subscription.payer?.let{ payer ->
                val payerEnrolmentId = when(subscription.paidBy.code) {
                    PayerTypeCode.INSURED -> null
                    else -> payer.cnamNumber.value
                }
                naturalPerson {
                    //firstName(payer.firstName.value)
                    //lastname(payer.lastName.value)
                    if (payerEnrolmentId != null) {
                        enrolmentId(payerEnrolmentId)
                    }
                }
            }
        }
    fun fingerprints(fingerprints: List<Fingerprint>): ZetesEnrolment {
        fingerprints.forEach(::fingerprint)
        return this
    }
    private fun fingerprint(fingerprint: Fingerprint) {
        fingerprint(fingerprint.fingerIndex, Base64.encodeToString(fingerprint.image))
    }
    fun fingerprint(position: Int, capture: String): ZetesEnrolment {
        val fingerprint = Xml.createChildElement(fingerprints, FINGERPRINT)
        Xml.createChildElement(fingerprint, FINGERPRINT_POSITION).textContent = position.toString()
        Xml.createChildElement(fingerprint, FINGERPRINT_CAPTURE).textContent = capture
        return this
    }
    fun photo(photo: String): ZetesEnrolment = text(arrayOf(PHOTO, PHOTO_CAPTURE), photo)
    fun numberOfScannedDocuments(numberOfScannedDocuments: Int): ZetesEnrolment =
        text(NUMBER_OF_SCANNED_DOCUMENTS, numberOfScannedDocuments.toString())
    fun signature(signature: String): ZetesEnrolment = text(arrayOf(SIGNATURE, SIGNATURE_CAPTURE), signature)
}
