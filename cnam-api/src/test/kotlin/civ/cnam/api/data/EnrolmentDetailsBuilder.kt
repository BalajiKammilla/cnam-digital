package civ.cnam.api.data

import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.type.enrolment.partial.AddressData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.BirthDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ContactDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.NaturalPersonPayerData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.PersonalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ProfessionalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import civ.cnam.enrolment.domain.model.value.contact.LandlineNumber
import civ.cnam.enrolment.domain.model.value.document.BirthCertificateNumber
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.core.data.model.value.MobileNumber
import net.datafaker.Faker
import java.time.LocalDate
import java.util.*

class EnrolmentDetailsBuilder : BaseBuilder() {
    /* +225 01 xx xx xx xx / +225 05 xx xx xx xx / +225 07 xx xx xx xx / +225 25 xx xx xx xx */
    private var mobileNumber: MobileNumber = mobileNumber()
    fun mobileNumber(value: String): EnrolmentDetailsBuilder {
        mobileNumber = MobileNumber(value)
        return this
    }

    private var dateOfBirth: LocalDate = faker.date()
        .birthday(20, 25)
        .toLocalDateTime()
        .toLocalDate()
    fun dateOfBirth(value: LocalDate): EnrolmentDetailsBuilder {
        dateOfBirth = value
        return this
    }
    fun age(value: Long): EnrolmentDetailsBuilder {
        dateOfBirth = LocalDate.now().minusYears(value)
        return this
    }

    private var firstNames: String = faker.name().firstName()
    fun firstNames(value: String): EnrolmentDetailsBuilder {
        firstNames = value
        return this
    }

    private var lastName: String = faker.name().lastName()
    fun lastName(value: String): EnrolmentDetailsBuilder {
        lastName = value
        return this
    }

    /**
     * M,Monsieur
     * MME,Madame
     * MLE,Mademoiselle
     */
    private var titleCode: TitleCode = TitleCode("M")
    fun titleCode(value: String): EnrolmentDetailsBuilder {
        titleCode = TitleCode(value)
        return this
    }

    private var nationalityCode: IsoAlpha3CountryCode = IsoAlpha3CountryCode(IsoCountryCode.CI.alpha3Code)
    fun nationality(value: IsoCountryCode): EnrolmentDetailsBuilder {
        nationalityCode = IsoAlpha3CountryCode(value.alpha3Code)
        return this
    }

    /**
     * CEL,Célibataire
     * MAR,Marié(e)
     * DIV,Divorcé(e)
     * 5VEU,Veuf / Veuve
     * CCB,Concubin(e)
     */
    private var maritalStatusCode: MaritalStatusCode = MaritalStatusCode("CEL")
    fun maritalStatusCode(value: String): EnrolmentDetailsBuilder {
        maritalStatusCode = MaritalStatusCode(value)
        return this
    }

    private var maidenName: String? = null
    fun maidenName(value: String): EnrolmentDetailsBuilder {
        maidenName = value
        return this
    }

    /**
     * FCI,Fonctionnaire civil
     * MIL,Militaire
     * REP,Retraité(e ) CGRAE
     **/
    private var personTypeCode: PersonCategoryCode = PersonCategoryCode("MIL")
    fun personTypeCode(value: String): EnrolmentDetailsBuilder {
        personTypeCode = PersonCategoryCode(value)
        return this
    }

    private var professionCode: ProfessionCode = ProfessionCode("11")
    fun professionCode(value: String): EnrolmentDetailsBuilder {
        professionCode = ProfessionCode(value)
        return this
    }

    /**
     * MCNPS,Matricule CNPS
     * MFP,Matricule Fonctionnaire
     * MIL,Matricule Militaire
     **/
    private var registrationNumberTypeCode: RegistrationNumberTypeCode = RegistrationNumberTypeCode("MIL")
    fun registrationNumberTypeCode(value: String): EnrolmentDetailsBuilder {
        registrationNumberTypeCode = RegistrationNumberTypeCode(value)
        return this
    }

    private var registrationNumber: String = faker.idNumber().ssnValid()
    fun registrationNumber(value: String): EnrolmentDetailsBuilder {
        registrationNumber = value
        return this
    }

    /**
     * 1,ASS,Assuré
     * 4,FRS,Frère ou sœur
     * 2,CJT,Conjoint(e )
     * 3,PER,Père
     * 3,MER,Mère
     * 5,TUT,Tuteur
     * 6,ENF,Enfant
     * 7,AUT,Autre
     */
    private var paidByCode: PayerTypeCode = PayerTypeCode("ASS")
    fun paidByCode(value: String): EnrolmentDetailsBuilder {
        paidByCode = PayerTypeCode(value)
        return this
    }

    private var payerCnamNumber = CNAMNumber(faker.idNumber().ssnValid())
    fun payerCnamNumber(value: String): EnrolmentDetailsBuilder {
        payerCnamNumber = CNAMNumber(value)
        return this
    }

    private var payerEnrolmentId = EnrolmentId(UUID.randomUUID().toString())
    fun payerEnrolmentId(value: String): EnrolmentDetailsBuilder {
        payerEnrolmentId = EnrolmentId(value)
        return this
    }

    private var payerFirstName = FirstName(faker.name().firstName())
    fun payerFirstName(value: String): EnrolmentDetailsBuilder {
        payerFirstName = FirstName(value)
        return this
    }

    private var payerLastName = LastName(faker.name().lastName())
    fun payerLastName(value: String): EnrolmentDetailsBuilder {
        payerLastName = LastName(value)
        return this
    }

    fun build(): EnrolmentDetailsData {
        return EnrolmentDetailsData(
            personalDetails = PersonalDetailsData(
                titleCode = titleCode,
                firstNames = FirstName(firstNames),
                lastName = LastName(lastName),
                currentNationalityCode = nationalityCode,
                maritalStatusCode = maritalStatusCode,
                maidenName = maidenName,
            ),
            birthDetails = BirthDetailsData(
                date = dateOfBirth,
                certificateNumber = BirthCertificateNumber("B12345"),
                certificateIssueDate = dateOfBirth.plusDays(5),
                countryCode = nationalityCode,
                subPrefectureCode = SubPrefectureCode("BAKO"),
            ),
            address = AddressData(
                subPrefectureCode = SubPrefectureCode("BAKO"),
                agencyCode = AgencyCode("ABGR01")
            ),
            contactDetails = ContactDetailsData(
                mobileNumber = mobileNumber,
                landlineNumber = LandlineNumber("027" + mobileNumber.value.takeLast(8)),
                email = EmailAddress("${firstNames.lowercase().replace(" ", ".")}@mail-host.civ"),
                postOfficeBox = null
            ),
            professionalDetails = ProfessionalDetailsData(
                personTypeCode = personTypeCode,
                professionCode = professionCode,
                employerCompanyCode = CompanyCode("CNP119985"),
                registrationNumberTypeCode = registrationNumberTypeCode,
                registrationNumber = registrationNumber,
            ),
            subscriptionDetails = SubscriptionDetailsData(
                paidByCode = paidByCode,
                payer = NaturalPersonPayerData(
                    cnamNumber = payerCnamNumber,
                    enrolmentId = payerEnrolmentId,
                    firstName = payerFirstName,
                    lastName = payerLastName,
                )
            )
        )
    }

    companion object {
        var faker = Faker(Locale("fr", "CIV"))
    }
}
