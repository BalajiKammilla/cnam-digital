package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.ProfessionValue
import civ.cnam.enrolment.domain.model.referencedata.Profession
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import civ.cnam.enrolment.domain.model.repository.referencedata.ProfessionRepository
import java.io.InputStream

class CsvProfessionRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Profession, ProfessionCode>(inputStream, 3, ProfessionValue::construct),
    ProfessionRepository
