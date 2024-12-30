package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.DocumentType
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatus
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import civ.cnam.enrolment.domain.model.referencedata.PayerType
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberType
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.repository.referencedata.AgencyRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.CompanyRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.CountryRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.DocumentTypeRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.MaritalStatusRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.NationalityRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.PayerTypeRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.PersonCategoryRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.ProfessionRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.RegistrationNumberTypeRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.SubPrefectureRepository
import civ.cnam.enrolment.domain.model.repository.referencedata.TitleRepository
import civ.cnam.enrolment.domain.model.type.enrolment.completed.Address
import civ.cnam.enrolment.domain.model.type.enrolment.completed.BirthDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.ContactDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.EnrolmentDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.NaturalPersonPayer
import civ.cnam.enrolment.domain.model.type.enrolment.completed.PersonalDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.ProfessionalDetails
import civ.cnam.enrolment.domain.model.type.enrolment.completed.SubscriptionDetails
import dev.dry.common.error.toError
import dev.dry.common.exception.CodedException
import dev.dry.common.text.message.MapParameterValueResolver
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

interface MapEnrolmentDetailsInputToEnrolmentDetails {
    operator fun invoke(input: civ.cnam.enrolment.domain.model.entity.EnrolmentDetails): EnrolmentDetails

    companion object {
        @Produces
        @Singleton
        @IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
        operator fun invoke(
            agencyRepository: AgencyRepository,
            companyRepository: CompanyRepository,
            registrationNumberTypeRepository: RegistrationNumberTypeRepository,
            countryRepository: CountryRepository,
            documentTypeRepository: DocumentTypeRepository,
            maritalStatusRepository: MaritalStatusRepository,
            nationalityRepository: NationalityRepository,
            payerTypeRepository: PayerTypeRepository,
            personCategoryRepository: PersonCategoryRepository,
            professionRepository: ProfessionRepository,
            subPrefectureRepository: SubPrefectureRepository,
            titleRepository: TitleRepository,
        ): MapEnrolmentDetailsInputToEnrolmentDetails {
            return object : MapEnrolmentDetailsInputToEnrolmentDetails {
                override fun invoke(input: civ.cnam.enrolment.domain.model.entity.EnrolmentDetails): EnrolmentDetails = EnrolmentDetails(
                    personalDetails = PersonalDetails(
                        title = lookupTitle(input.personalDetails.titleCode),
                        firstNames = input.personalDetails.firstNames,
                        lastName = input.personalDetails.lastName,
                        currentNationality = lookupNationality(input.personalDetails.currentNationalityCode),
                        maritalStatus = lookupMaritalStatus(input.personalDetails.maritalStatusCode),
                        maidenName = input.personalDetails.maidenName,
                    ),
                    birthDetails = BirthDetails(
                        date = input.birthDetails.date,
                        certificateNumber = input.birthDetails.certificateNumber,
                        certificateIssueDate = input.birthDetails.certificateIssueDate,
                        country = lookupCountry(input.birthDetails.countryCode),
                        subPrefecture = input.birthDetails.subPrefectureCode?.let(::lookupSubPrefectureOrNull),
                    ),
                    address = Address(
                        subPrefecture = lookupSubPrefecture(input.address.subPrefectureCode),
                        agency = lookupAgency(input.address.agencyCode),
                    ),
                    contactDetails = ContactDetails(
                        mobileNumber = input.contactDetails.mobileNumber,
                        landlineNumber = input.contactDetails.landlineNumber,
                        email = input.contactDetails.email,
                        postOfficeBox = input.contactDetails.postOfficeBox,
                    ),
                    professionalDetails = ProfessionalDetails(
                        personCategory = lookupPersonCategory(input.professionalDetails.personTypeCode),
                        // TODO("validate non-null employerCompanyCode")
                        employer = input.professionalDetails.employerCompanyCode?.let(::lookupCompanyOrNull),
                        registrationNumberType = input.professionalDetails.registrationNumberTypeCode?.let(
                            ::lookupRegistrationNumberTypeOrNull
                        ),
                        registrationNumber = input.professionalDetails.registrationNumber,
                        profession = input.professionalDetails.professionCode?.let(::lookupProfessionOrNull)
                    ),
                    subscriptionDetails = SubscriptionDetails(
                        paidBy = lookupPayerType(input.subscriptionDetails.paidByCode),
                        payer = input.subscriptionDetails.payer?.let {
                            it.run {
                                NaturalPersonPayer(
                                    cnamNumber = cnamNumber,
                                    enrolmentId = this.enrolmentId,
                                    firstName = firstName,
                                    lastName = lastName,
                                )
                            }
                        }
                    )
                )

                private fun referenceValueNotFound(referenceValueType: String, code: Any): CodedException {
                    return EnrolmentErrors.REFERENCE_VALUE_NOT_FOUND_WITH_CODE.toError(
                        MapParameterValueResolver.parameters(
                            "type" to referenceValueType,
                            "code" to code
                        )
                    ).toException()
                }

                private fun lookupDocumentType(code: DocumentTypeCode): DocumentType {
                    return documentTypeRepository.findByCode(code)
                        ?: throw referenceValueNotFound("document type", code.value)
                }

                private fun lookupTitle(code: TitleCode): Title {
                    return titleRepository.findByCode(code)
                        ?: throw referenceValueNotFound("title", code.value)
                }

                fun lookupNationality(code: IsoAlpha3CountryCode): Nationality {
                    return nationalityRepository.findByCode(code)
                        ?: throw referenceValueNotFound("nationality", code.value)
                }

                private fun lookupCountry(code: IsoAlpha3CountryCode): Country {
                    return countryRepository.findByCode(code)
                        ?: throw referenceValueNotFound("country", code.value)
                }

                private fun lookupMaritalStatus(code: MaritalStatusCode): MaritalStatus {
                    return maritalStatusRepository.findByCode(code)
                        ?: throw referenceValueNotFound("marital status", code.value)
                }

                private fun lookupSubPrefecture(code: SubPrefectureCode): SubPrefecture {
                    return lookupSubPrefectureOrNull(code)
                        ?: throw referenceValueNotFound("sub-prefecture", code.value)
                }

                private fun lookupSubPrefectureOrNull(code: SubPrefectureCode): SubPrefecture? {
                    return subPrefectureRepository.findByCode(code)
                }

                private fun lookupPersonCategory(code: PersonCategoryCode): PersonCategory {
                    return personCategoryRepository.findByCode(code)
                        ?: throw referenceValueNotFound("person category", code.value)
                }

                private fun lookupCompanyOrNull(code: CompanyCode): Company? {
                    return companyRepository.findByCode(code)
                }

                private fun lookupRegistrationNumberTypeOrNull(code: RegistrationNumberTypeCode): RegistrationNumberType? {
                    return registrationNumberTypeRepository.findByCode(code)
                }

                private fun lookupProfession(code: ProfessionCode): Profession {
                    return lookupProfessionOrNull(code)
                        ?: throw referenceValueNotFound("profession", code.value)
                }

                private fun lookupProfessionOrNull(code: ProfessionCode): Profession? {
                    return professionRepository.findByCode(code)
                }

                private fun lookupPayerType(code: PayerTypeCode): PayerType {
                    return payerTypeRepository.findByCode(code)
                        ?: throw referenceValueNotFound("payer type", code.value)
                }

                private fun lookupAgency(code: AgencyCode): Agency {
                    return agencyRepository.findByCode(code)
                        ?: throw referenceValueNotFound("agency", code.value)
                }
            }
        }
    }
}
