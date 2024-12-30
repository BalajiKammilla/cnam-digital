package civ.cnam.enrolment.domain.model.repository.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import dev.dry.core.data.pagination.Page
import dev.dry.core.data.pagination.PageRequest

interface CompanyRepository: ReferenceDataRepository<Company, CompanyCode> {
    fun findByLabelLike(labelSearchTerm: String, pageRequest: PageRequest): Page<Company>
}
