package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.CodedLabel

@JvmInline
value class RegistrationNumberTypeCode(val value: String) {
    companion object {
        const val MCNPS = "MCNPS"
        const val MFP = "MFP"
        const val MIL = "MIL"
    }
}

interface RegistrationNumberType : CodedLabel<RegistrationNumberType, RegistrationNumberTypeCode>
