package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class ProfessionCode(val value: String)

interface Profession : OrderedCodedLabel<Profession, ProfessionCode>
