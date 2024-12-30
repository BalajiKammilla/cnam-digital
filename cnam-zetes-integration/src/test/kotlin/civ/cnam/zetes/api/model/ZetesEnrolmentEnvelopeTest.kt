package civ.cnam.zetes.api.model

import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope.Companion.ENCRYPTED_REQUEST
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope.Companion.ENROLLMENT_SERVICE_VERSION
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope.Companion.ENROLLMENT_SITE
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope.Companion.REQUEST_IDENTIFIER
import dev.dry.common.io.encoding.Base64
import dev.dry.core.data.format.xml.Xml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZetesEnrolmentEnvelopeTest : BaseXmlTest() {
    companion object {
        private const val REQUEST_IDENTIFIER_VALUE = "cb94186c-fd3f-4019-bdac-26abd71a68c6"
        private const val ENROLLMENT_SITE_VALUE = "DAT"
        private const val ENROLLMENT_SERVICE_VERSION_VALUE = "SNEDAI.1.0"
        private const val ENCRYPTED_REQUEST_VALUE = "123123123123123123123"
        private val ENCRYPTED_REQUEST_VALUE_BYTES = ENCRYPTED_REQUEST_VALUE.toByteArray()
    }

    @Test
    fun test() {
        val envelope = ZetesEnrolmentEnvelope.newInstance()
            .requestIdentifier(REQUEST_IDENTIFIER_VALUE)
            .enrollmentSite(ENROLLMENT_SITE_VALUE)
            .enrollmentServiceVersion(ENROLLMENT_SERVICE_VERSION_VALUE)
            .encryptedRequest(ENCRYPTED_REQUEST_VALUE_BYTES)
            .toXmlString()
            .let(Xml.Transform::toDocument)
            .documentElement
        assertEquals(REQUEST_IDENTIFIER_VALUE, childText(envelope, REQUEST_IDENTIFIER))
        assertEquals(ENROLLMENT_SITE_VALUE, childText(envelope, ENROLLMENT_SITE))
        assertEquals(ENROLLMENT_SERVICE_VERSION_VALUE, childText(envelope, ENROLLMENT_SERVICE_VERSION))
        assertEquals(ENCRYPTED_REQUEST_VALUE, String(Base64.decodeFromString(childText(envelope, ENCRYPTED_REQUEST))))
    }
}