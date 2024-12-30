package civ.cnam.enrolment.domain.model.repository

import civ.cnam.enrolment.domain.model.entity.EnrolmentUser
import dev.dry.core.data.model.value.MobileNumber

interface EnrolmentUserRepository {
    fun findByMobileNumber(mobileNumber: MobileNumber): EnrolmentUser
}