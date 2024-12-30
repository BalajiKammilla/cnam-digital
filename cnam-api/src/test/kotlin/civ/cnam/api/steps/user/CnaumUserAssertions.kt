package civ.cnam.api.steps.user

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.assertj.hasStringEntrySatisfying
import org.assertj.core.api.AbstractMapAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.MapAssert

fun isCnamUserAccount(
    userAccount: Map<String, Any>?
): AbstractMapAssert<MapAssert<String, Any>, MutableMap<String, Any>, String, Any> {
    return Assertions.assertThat(userAccount)
        .`as`("CNAM User Account")
        .isNotNull
        .hasStringEntrySatisfying("realmName") { Assertions.assertThat(it).isEqualTo(RealmNames.CNAM) }
        .hasStringEntrySatisfying("userName") { Assertions.assertThat(it).isNotBlank }
        .hasStringEntrySatisfying("displayName") { Assertions.assertThat(it).isNotBlank }
        .hasStringEntrySatisfying("emailAddress") { Assertions.assertThat(it).isEqualTo(userAccount?.get("userName")) }
        .hasEntrySatisfying("mobileNumber") { Assertions.assertThat(it).isNull() }
        .hasStringEntrySatisfying("status") { Assertions.assertThat(it).isNotBlank }
        .hasStringEntrySatisfying("alertChannel") { Assertions.assertThat(it).isEqualTo(AlertChannel.EMAIL.name) }
        .hasStringEntrySatisfying("createdAt") { Assertions.assertThat(it).isNotBlank }
}