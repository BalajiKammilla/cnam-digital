package civ.cnam.enrolment.constants

import dev.dry.core.data.model.value.RealmName

object RealmNames {
    const val SYSTEM = "system"
    val SYSTEM_VALUE = RealmName(SYSTEM)
    const val CNAM = "cnam"
    val CNAM_VALUE = RealmName(CNAM)
    const val PARTNER = "partner"
    val PARTNER_VALUE = RealmName(PARTNER)
    const val PUBLIC = "public"
    val PUBLIC_VALUE = RealmName(PUBLIC)
}
