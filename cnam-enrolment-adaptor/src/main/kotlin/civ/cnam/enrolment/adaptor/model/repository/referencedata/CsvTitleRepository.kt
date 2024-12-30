package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.TitleValue
import civ.cnam.enrolment.domain.model.referencedata.Title
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.repository.referencedata.TitleRepository
import java.io.InputStream

class CsvTitleRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Title, TitleCode>(inputStream, 2, TitleValue::construct),
    TitleRepository
