package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.OrderedCodedLabel

interface Nationality : OrderedCodedLabel<Nationality, IsoAlpha3CountryCode>
