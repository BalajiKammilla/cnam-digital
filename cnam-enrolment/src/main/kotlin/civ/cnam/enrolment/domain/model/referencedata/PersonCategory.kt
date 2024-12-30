package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.OrderedCodedLabel

@JvmInline
value class PersonCategoryCode(val value: String)

interface PersonCategory : OrderedCodedLabel<PersonCategory, PersonCategoryCode>
