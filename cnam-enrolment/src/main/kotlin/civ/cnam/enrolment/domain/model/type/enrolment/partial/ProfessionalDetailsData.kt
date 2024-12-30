package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.ProfessionalDetails
import civ.cnam.enrolment.domain.model.referencedata.CompanyCode
import civ.cnam.enrolment.domain.model.referencedata.PersonCategoryCode
import civ.cnam.enrolment.domain.model.referencedata.ProfessionCode
import civ.cnam.enrolment.domain.model.referencedata.RegistrationNumberTypeCode
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
class ProfessionalDetailsData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val personTypeCode: PersonCategoryCode,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val professionCode: ProfessionCode?,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val employerCompanyCode: CompanyCode?,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val registrationNumberTypeCode: RegistrationNumberTypeCode?,
    override val registrationNumber: String?,
) : ProfessionalDetails {
    constructor() : this(
        personTypeCode = PersonCategoryCode(""),
        professionCode = null,
        employerCompanyCode = null,
        registrationNumberTypeCode = null,
        registrationNumber = null,
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(
            personTypeCode: String,
            professionCode: String?,
            employerCompanyCode: String?,
            registrationNumberTypeCode: String?,
            registrationNumber: String?,
        ) = ProfessionalDetailsData(
            personTypeCode = PersonCategoryCode(personTypeCode),
            professionCode = professionCode?.let(::ProfessionCode),
            employerCompanyCode = employerCompanyCode?.let(::CompanyCode),
            registrationNumberTypeCode = registrationNumberTypeCode?.let(::RegistrationNumberTypeCode),
            registrationNumber = registrationNumber,
        )
    }
}
