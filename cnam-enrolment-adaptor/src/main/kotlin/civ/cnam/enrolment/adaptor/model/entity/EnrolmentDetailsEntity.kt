package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.type.enrolment.partial.AddressData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.BirthDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.PersonalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ProfessionalDetailsData
import civ.cnam.enrolment.domain.model.entity.EnrolmentDetails
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ContactDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity(name = "EnrolmentDetails")
class EnrolmentDetailsEntity(
    @JoinColumn(
        name = "id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_details"),
    )
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    var enrolment: PartialEnrolmentEntity,

    @Id
    @Convert(converter = ValueClassLongAttributeConverter::class)
    @JsonIgnore
    val id: PartialEnrolment.ID = enrolment.id,

    @Embedded
    override var personalDetails: PersonalDetailsData,

    @Embedded
    @AttributeOverrides(value = [
        AttributeOverride(name = "subPrefectureCode", column = Column(name = "birth_sub_prefecture_code")),
    ])
    override var birthDetails: BirthDetailsData,
    @Embedded
    override var address: AddressData,
    @Embedded
    override var contactDetails: ContactDetailsData,
    @Embedded
    override var professionalDetails: ProfessionalDetailsData,
    @Embedded
    override var subscriptionDetails: SubscriptionDetailsData
) : EnrolmentDetails
