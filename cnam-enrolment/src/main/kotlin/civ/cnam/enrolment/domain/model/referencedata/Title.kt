package civ.cnam.enrolment.domain.model.referencedata

import dev.dry.core.data.model.referencedata.CodedLabel

@JvmInline
value class TitleCode(val value: String) {
    companion object {
        val MISTER = TitleCode("M")
        val MADAM = TitleCode("MME")
        val MISS = TitleCode("MLE")
    }
}

interface Title : CodedLabel<Title, TitleCode>
