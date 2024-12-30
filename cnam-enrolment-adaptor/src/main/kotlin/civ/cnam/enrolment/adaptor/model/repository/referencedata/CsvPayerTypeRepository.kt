package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.PayerTypeValue
import civ.cnam.enrolment.domain.model.referencedata.PayerType
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import civ.cnam.enrolment.domain.model.repository.referencedata.PayerTypeRepository
import java.io.InputStream

class CsvPayerTypeRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<PayerType, PayerTypeCode>(inputStream, 3, PayerTypeValue::construct),
    PayerTypeRepository
