package civ.cnam.enrolment.domain.model.query.referencedata

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.DocumentType
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatus
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import civ.cnam.enrolment.domain.model.referencedata.PayerType
import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberType
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.repository.referencedata.AgencyRepository
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
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetReferenceDatasets(
    private val agencyRepository: AgencyRepository,
    private val countryRepository: CountryRepository,
    private val documentTypeRepository: DocumentTypeRepository,
    private val maritalStatusRepository: MaritalStatusRepository,
    private val nationalityRepository: NationalityRepository,
    private val payerTypeRepository: PayerTypeRepository,
    private val personCategoryRepository: PersonCategoryRepository,
    private val professionRepository: ProfessionRepository,
    private val subPrefectureRepository: SubPrefectureRepository,
    private val titleRepository: TitleRepository,
    private val registrationNumberTypeRepository: RegistrationNumberTypeRepository,
) {
    class ReferenceDatasets(
        val agencies: List<Agency> = emptyList(),
        val countries: List<Country> = emptyList(),
        val documentTypes: List<DocumentType> = emptyList(),
        val maritalStatuses: List<MaritalStatus> = emptyList(),
        val nationalities: List<Nationality> = emptyList(),
        val payerTypes: List<PayerType> = emptyList(),
        val personCategories: List<PersonCategory> = emptyList(),
        val professions: List<Profession> = emptyList(),
        val subPrefectures: List<SubPrefecture> = emptyList(),
        val titles: List<Title> = emptyList(),
        val registrationNumberTypes: List<RegistrationNumberType> = emptyList(),
    )

    private var referenceDatasets: ReferenceDatasets? = null

    operator fun invoke(): ReferenceDatasets {
        return referenceDatasets ?: ReferenceDatasets(
            agencies = agencyRepository.findAll(),
            countries = countryRepository.findAll(),
            documentTypes = documentTypeRepository.findAll(),
            maritalStatuses = maritalStatusRepository.findAll(),
            nationalities = nationalityRepository.findAll(),
            payerTypes = payerTypeRepository.findAll(),
            personCategories = personCategoryRepository.findAll(),
            professions = professionRepository.findAll(),
            subPrefectures = subPrefectureRepository.findAll(),
            titles = titleRepository.findAll(),
            registrationNumberTypes = registrationNumberTypeRepository.findAll(),
        ).also { referenceDatasets = it }
    }
}
