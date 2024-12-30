package civ.cnam.enrolment.adaptor.configuration

import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvAgencyRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvCompanyRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvCountryRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvDocumentTypeRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvMaritalStatusRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvNationalityRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvPayerTypeRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvPersonCategoryRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvProfessionRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvRegistrationNumberTypeRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvRepositoryFactory
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvSubPrefectureRepository
import civ.cnam.enrolment.adaptor.model.repository.referencedata.CsvTitleRepository
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class ReferenceDataRepositoryProducer {
    @Produces
    @Singleton
    fun agencyRepository(): CsvAgencyRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun companyRepository(): CsvCompanyRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun countryRepository(): CsvCountryRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun documentTypeRepository(): CsvDocumentTypeRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun maritalStatusRepository(): CsvMaritalStatusRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun nationalityRepository(): CsvNationalityRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun payerTypeRepository(): CsvPayerTypeRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun personCategoryRepository(): CsvPersonCategoryRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun professionRepository(): CsvProfessionRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun registrationNumberTypeRepository(): CsvRegistrationNumberTypeRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun subPrefectureRepository(): CsvSubPrefectureRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }

    @Produces
    @Singleton
    fun titleRepository(): CsvTitleRepository {
        return CsvRepositoryFactory.constructCsvRepository()
    }
}
