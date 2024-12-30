package civ.cnam.enrolment.domain.model.referencedata

import com.fasterxml.jackson.annotation.JsonValue
import dev.dry.core.data.model.referencedata.CodedLabel

@JvmInline
value class CompanyCode(@JsonValue val value: String)

interface Company : CodedLabel<Company, CompanyCode>
