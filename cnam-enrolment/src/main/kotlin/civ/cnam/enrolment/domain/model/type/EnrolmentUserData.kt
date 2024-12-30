package civ.cnam.enrolment.domain.model.type

import civ.cnam.enrolment.domain.model.entity.EnrolmentUser
import dev.dry.core.data.model.value.RealmName
import dev.dry.core.data.model.value.UserName

class EnrolmentUserData(
    val userType: RealmName,
    val userName: UserName,
    val displayName: String,
) {
    constructor(user: EnrolmentUser): this(
        userType = user.realmName,
        userName = user.userName,
        displayName = user.displayName,
    )
}
