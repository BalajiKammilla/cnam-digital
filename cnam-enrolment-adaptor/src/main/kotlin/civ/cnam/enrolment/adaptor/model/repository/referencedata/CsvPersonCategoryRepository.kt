package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.PersonCategoryValue
import civ.cnam.enrolment.domain.model.referencedata.PersonCategory
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.repository.referencedata.PersonCategoryRepository
import java.io.InputStream

class CsvPersonCategoryRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<PersonCategory, PersonCategoryCode>(inputStream, 3, PersonCategoryValue::construct),
    PersonCategoryRepository
