package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.LegalPersonPayer
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class LegalPersonPayerData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val cnamNumber: CNAMNumber,
    override val businessEntityType: String,
    override val cc: String,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val companyCode: CompanyCode,
    override val creationDate: LocalDate,
): LegalPersonPayer {
    constructor() : this(
        cnamNumber = CNAMNumber(""),
        businessEntityType = "",
        cc = "",
        companyCode = CompanyCode(""),
        creationDate = LocalDate.MIN,
    )
}
