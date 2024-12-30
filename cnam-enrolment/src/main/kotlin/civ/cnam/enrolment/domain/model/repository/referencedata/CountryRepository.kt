package civ.cnam.enrolment.domain.model.repository.referencedata

import civ.cnam.enrolment.domain.model.referencedata.Country
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode

interface CountryRepository: ReferenceDataRepository<Country, IsoAlpha3CountryCode>
