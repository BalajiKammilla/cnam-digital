package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.CompanyValue
import civ.cnam.enrolment.domain.model.referencedata.Company
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.repository.referencedata.CompanyRepository
import dev.dry.core.data.pagination.Page
import dev.dry.core.data.pagination.PageRequest
import java.io.InputStream

class CsvCompanyRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<Company, CompanyCode>(inputStream, 2, CompanyValue::construct),
    CompanyRepository {
    override fun findByLabelLike(labelSearchTerm: String, pageRequest: PageRequest): Page<Company> {
        val upperCaseLabelSearchTerm = labelSearchTerm.uppercase()
        val filteredValues = values.filter { it.label.contains(upperCaseLabelSearchTerm) }
        return Page.from(filteredValues, pageRequest)
    }
}
