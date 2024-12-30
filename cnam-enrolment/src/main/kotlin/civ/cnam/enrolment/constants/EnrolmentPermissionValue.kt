package civ.cnam.enrolment.constants

import dev.dry.core.constants.PermissionValueConstants

object EnrolmentPermissionValue : PermissionValueConstants {
    const val ENROLMENT_READ_VALUE = "enrolment:read"
    const val ENROLMENT_UPDATE_VALUE = "enrolment:update"
    const val ENROLMENT_APPROVE_VALUE = "enrolment:approve"
    const val ENROLMENT_LIST_VALUE = "enrolment:list"
    const val DEDUPE_LIST_VALUE = "dedupe:list"

    const val DASHBOARD_READ_VALUE = "dashboard:read"
}