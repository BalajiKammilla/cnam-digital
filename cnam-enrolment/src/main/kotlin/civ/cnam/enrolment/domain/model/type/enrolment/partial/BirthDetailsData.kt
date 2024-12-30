package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.BirthDetails
import civ.cnam.enrolment.domain.model.referencedata.IsoAlpha3CountryCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.value.document.BirthCertificateNumber
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class BirthDetailsData(
    override var date: LocalDate,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val certificateNumber: BirthCertificateNumber,
    override val certificateIssueDate: LocalDate,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val countryCode: IsoAlpha3CountryCode,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val subPrefectureCode: SubPrefectureCode?,
) : BirthDetails {
    constructor() : this(
        date = LocalDate.MIN,
        certificateNumber = BirthCertificateNumber(""),
        certificateIssueDate = LocalDate.MIN,
        countryCode = IsoAlpha3CountryCode(""),
        subPrefectureCode = null
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(
            date: LocalDate,
            certificateNumber: String,
            certificateIssueDate: LocalDate,
            countryCode: String,
            subPrefectureCode: String?,
        ) = BirthDetailsData(
            date = date,
            certificateNumber = BirthCertificateNumber(certificateNumber),
            certificateIssueDate = certificateIssueDate,
            countryCode = IsoAlpha3CountryCode(countryCode),
            subPrefectureCode = subPrefectureCode?.let(::SubPrefectureCode),
        )
    }
}
