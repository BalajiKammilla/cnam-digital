package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.NationalityValue
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.Nationality
import civ.cnam.enrolment.domain.model.repository.referencedata.NationalityRepository
import java.io.InputStream

class CsvNationalityRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Nationality, IsoAlpha3CountryCode>(inputStream, 3, NationalityValue::construct),
    NationalityRepository
