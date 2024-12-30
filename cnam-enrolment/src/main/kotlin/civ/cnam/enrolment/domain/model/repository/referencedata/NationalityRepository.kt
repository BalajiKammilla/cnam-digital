package civ.cnam.enrolment.domain.model.repository.referencedata

import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.Nationality

interface NationalityRepository: ReferenceDataRepository<Nationality, IsoAlpha3CountryCode>
