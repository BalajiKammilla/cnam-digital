package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.EnrolmentDeviceSession
import dev.dry.core.data.model.type.DeviceAttributesData
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "EnrolmentDeviceSession")
class EnrolmentDeviceSessionEntity(
    @Id
    override val id: Long,
    @Embedded
    override val deviceAttributes: DeviceAttributesData
) : EnrolmentDeviceSession
