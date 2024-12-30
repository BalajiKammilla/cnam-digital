package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.SubPrefectureValue
import civ.cnam.enrolment.domain.model.referencedata.SubPrefecture
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.repository.referencedata.SubPrefectureRepository
import java.io.InputStream

class CsvSubPrefectureRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<SubPrefecture, SubPrefectureCode>(inputStream, 2, SubPrefectureValue::construct),
    SubPrefectureRepository
