package civ.cnam.enrolment.domain.model.repository.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Agency
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode

interface AgencyRepository: ReferenceDataRepository<Agency, AgencyCode> {
    fun findBySubPrefectureCode(subPrefectureCode: SubPrefectureCode): List<Agency>
}
