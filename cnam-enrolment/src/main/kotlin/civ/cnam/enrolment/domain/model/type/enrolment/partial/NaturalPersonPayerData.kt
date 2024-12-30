package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.NaturalPersonPayer
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
class NaturalPersonPayerData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val cnamNumber: CNAMNumber,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val enrolmentId: EnrolmentId,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val firstName: FirstName,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val lastName: LastName,
): NaturalPersonPayer {
    constructor() : this(
        cnamNumber = CNAMNumber(""),
        enrolmentId = EnrolmentId(""),
        firstName = FirstName(""),
        lastName = LastName(""),
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun construct(
            cnamNumber: String,
            enrolmentId: String,
            firstName: String,
            lastName: String,
        ) = NaturalPersonPayerData(
            cnamNumber = CNAMNumber(cnamNumber),
            enrolmentId = EnrolmentId(enrolmentId),
            firstName = FirstName(firstName),
            lastName = LastName(lastName),
        )
    }
}
