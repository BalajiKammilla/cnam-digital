package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.CountryValue
import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.repository.referencedata.CountryRepository
import java.io.InputStream

class CsvCountryRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Country, IsoAlpha3CountryCode>(inputStream, 3, CountryValue::construct),
    CountryRepository
