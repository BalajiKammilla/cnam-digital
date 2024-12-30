package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.MaritalStatusValue
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatus
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.repository.referencedata.MaritalStatusRepository
import java.io.InputStream

class CsvMaritalStatusRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<MaritalStatus, MaritalStatusCode>(inputStream, 3, MaritalStatusValue::construct),
    MaritalStatusRepository
