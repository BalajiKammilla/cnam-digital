package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.EnrolmentUser
import dev.dry.core.data.model.entity.User
import dev.dry.core.data.model.value.EmailAddress
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.data.model.value.RealmName
import dev.dry.core.data.model.value.UserName
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "EnrolmentUser")
class EnrolmentUserEntity(
    @Id
    override val id: User.ID,
    @Column(nullable = false, insertable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val realmName: RealmName,
    @Column(nullable = false, insertable = false, updatable = false)
    override val userName: UserName,
    @Column(nullable = false, insertable = false, updatable = false)
    override val displayName: String,
    @Column(insertable = false, updatable = false)
    override val emailAddress: EmailAddress?,
    @Column(insertable = false, updatable = false)
    override val mobileNumber: MobileNumber?
) : EnrolmentUser
