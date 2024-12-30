package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class IsoAlpha3CountryCode(val value: String)

interface Country : OrderedCodedLabel<Country, IsoAlpha3CountryCode>
