package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.AgencyValue
import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.repository.referencedata.AgencyRepository
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import java.io.InputStream

class CsvAgencyRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Agency, AgencyCode>(inputStream, 4, AgencyValue::construct),
    AgencyRepository {
    override fun findBySubPrefectureCode(subPrefectureCode: SubPrefectureCode): List<Agency> {
        return values.filter { it.subPrefectureCode == subPrefectureCode }
    }
}
