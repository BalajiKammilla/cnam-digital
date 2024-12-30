package civ.cnam.zetes.api.client.function

import civ.cnam.zetes.api.client.model.ZetesEnrolment
import civ.cnam.zetes.api.client.model.ZetesEnrolmentEnvelope
import civ.cnam.zetes.api.client.model.ZetesEnrolmentSite

class PrepareEnrolmentRequestEnvelope(private val applyEncryption: ApplyEncryption) {
    operator fun invoke(
        enrolment: ZetesEnrolment,
        enrolmentSite: ZetesEnrolmentSite,
    ): ZetesEnrolmentEnvelope {
        val encryptedRequest = applyEncryption(enrolment.toXmlString())
        return ZetesEnrolmentEnvelope.newInstance()
            .enrollmentSite(enrolmentSite.toString())
            .requestIdentifier(enrolment.request.requestIdentifier)
            .encryptedRequest(encryptedRequest)
    }
}