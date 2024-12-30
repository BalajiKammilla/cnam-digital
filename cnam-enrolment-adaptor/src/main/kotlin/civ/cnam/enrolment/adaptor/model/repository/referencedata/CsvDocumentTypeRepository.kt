package civ.cnam.enrolment.adaptor.model.repository.referencedata

import civ.cnam.enrolment.adaptor.model.referencedata.DocumentTypeValue
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.repository.referencedata.DocumentTypeRepository
import civ.cnam.enrolment.domain.model.referencedata.DocumentType
import java.io.InputStream

class CsvDocumentTypeRepository(inputStream: InputStream) :
    BaseCsvReferenceDataRepository<DocumentType, DocumentTypeCode>(inputStream, 5, DocumentTypeValue::construct),
    DocumentTypeRepository
