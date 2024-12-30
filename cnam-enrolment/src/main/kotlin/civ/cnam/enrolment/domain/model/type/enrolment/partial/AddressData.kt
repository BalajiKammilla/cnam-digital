package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.Address
import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
class AddressData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val subPrefectureCode: SubPrefectureCode,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val agencyCode: AgencyCode,
) : Address {
    constructor() : this(
        subPrefectureCode = SubPrefectureCode(""),
        agencyCode = AgencyCode("")
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(subPrefectureCode: String, agencyCode: String) =
            AddressData(
                SubPrefectureCode(subPrefectureCode),
                AgencyCode(agencyCode)
            )
    }
}
