package civ.cnam.enrolment.domain.model.entity

import dev.dry.core.data.model.type.DeviceAttributes

interface EnrolmentDeviceSession {
    val id: Long
    val deviceAttributes: DeviceAttributes
}