package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.SubscriptionDetails
import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded

@Embeddable
class SubscriptionDetailsData(
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val paidByCode: PayerTypeCode,
    @Embedded
    @AttributeOverrides(value = [
        AttributeOverride(name = "firstName", column = Column(name = "payer_first_name")),
        AttributeOverride(name = "lastName", column = Column(name = "payer_last_name")),
    ])
    override val payer: NaturalPersonPayerData?
) : SubscriptionDetails {
    constructor() : this(
        paidByCode = PayerTypeCode(""),
        payer = null
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(
            paidByCode: String,
            payer: NaturalPersonPayerData?,
        ) = SubscriptionDetailsData(
            paidByCode = PayerTypeCode(paidByCode),
            payer = payer,
        )
    }
}
