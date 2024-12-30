package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.RegistrationNumberTypeValue
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberType
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import civ.cnam.enrolment.domain.model.repository.referencedata.RegistrationNumberTypeRepository
import java.io.InputStream

class CsvRegistrationNumberTypeRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<RegistrationNumberType, RegistrationNumberTypeCode>(
        inputStream, 2,
        RegistrationNumberTypeValue::construct
    ),
    RegistrationNumberTypeRepository
