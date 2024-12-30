package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.domain.model.repository.referencedata.ReferenceDataRepository
import dev.dry.core.data.format.csv.CsvReader
import dev.dry.core.data.model.referencedata.CodedLabel
import java.io.InputStream

open class BaseCsvReferenceDataRepository<T: CodedLabel<T, C>, C>(
    inputStream: InputStream,
    columns: Int,
    construct: (values: List<String>) -> T
): ReferenceDataRepository<T, C> {
    protected val values: List<T> = CsvReader.read(inputStream, true, columns, construct).sorted()

    private val valueByCode: Map<C, T> = values.associateBy { it.code }

    override fun findAll(): List<T> = values

    override fun findByCode(code: C): T? = valueByCode[code]
}
