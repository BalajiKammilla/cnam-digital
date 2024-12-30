package civ.cnam.enrolment.domain.model.repository.referencedata

interface ReferenceDataRepository<T, C> {
    fun findAll(): List<T>
    fun findByCode(code: C): T?
}
