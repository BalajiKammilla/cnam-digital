package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.PersonalDetails
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.MaritalStatusCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
class PersonalDetailsData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val titleCode: TitleCode,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var firstNames: FirstName,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var lastName: LastName,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val currentNationalityCode: IsoAlpha3CountryCode,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val maritalStatusCode: MaritalStatusCode,
    override val maidenName: String?,
) : PersonalDetails {
    constructor() : this(
        titleCode = TitleCode(""),
        firstNames = FirstName(""),
        lastName = LastName(""),
        currentNationalityCode = IsoAlpha3CountryCode(""),
        maritalStatusCode = MaritalStatusCode(""),
        maidenName = null,
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun construct(
            titleCode: String,
            firstNames: String,
            lastName: String,
            currentNationalityCode: String,
            maritalStatusCode: String,
            maidenName: String?,
        ) = PersonalDetailsData(
            titleCode = TitleCode(titleCode),
            firstNames = FirstName(firstNames),
            lastName = LastName(lastName),
            currentNationalityCode = IsoAlpha3CountryCode(currentNationalityCode),
            maritalStatusCode = MaritalStatusCode(maritalStatusCode),
            maidenName = maidenName,
        )
    }
}
