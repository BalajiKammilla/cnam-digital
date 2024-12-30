package civ.cnam.enrolment.domain.model.type.enrolment.partial

import civ.cnam.enrolment.domain.model.entity.ContactDetails
import civ.cnam.enrolment.domain.model.value.contact.EmailAddress
import civ.cnam.enrolment.domain.model.value.contact.LandlineNumber
import civ.cnam.enrolment.domain.model.value.contact.PostOfficeBox
import com.fasterxml.jackson.annotation.JsonCreator
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.data.validation.Mobile
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
class ContactDetailsData(
    @get:Mobile
    override val mobileNumber: MobileNumber,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val landlineNumber: LandlineNumber,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val email: EmailAddress?,
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val postOfficeBox: PostOfficeBox?,
) : ContactDetails {
    constructor(): this(
        mobileNumber = MobileNumber(""),
        landlineNumber = LandlineNumber(""),
        email = null,
        postOfficeBox = null
    )

    constructor(contactDetails: ContactDetailsData) : this(
        mobileNumber = contactDetails.mobileNumber.toInternationalFormat(IsoCountryCode.CI),
        landlineNumber = contactDetails.landlineNumber,
        email = contactDetails.email,
        postOfficeBox = contactDetails.postOfficeBox
    )

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(
            mobileNumber: String,
            landlineNumber: String,
            email: String?,
            postOfficeBox: String?,
        ) = ContactDetailsData(
            mobileNumber = MobileNumber(mobileNumber),
            landlineNumber = LandlineNumber(landlineNumber),
            email = email?.let(::EmailAddress),
            postOfficeBox = postOfficeBox?.let(::PostOfficeBox),
        )
    }
}
