package civ.cnam.zetes.api.client.function

import civ.cnam.enrolment.domain.model.type.attachment.Fingerprint
import civ.cnam.enrolment.domain.model.type.attachment.PhotoAttachment
import civ.cnam.enrolment.domain.model.type.attachment.SignatureAttachment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.zetes.api.client.model.ZetesEnrolment
import civ.cnam.zetes.api.client.model.ZetesEnrolmentSite
import dev.dry.common.io.encoding.Base64

class MapEnrolment {
    operator fun invoke(
        enrolment: CompletedEnrolment,
        enrolmentSite: ZetesEnrolmentSite,
        photo: PhotoAttachment,
        fingerprints: List<Fingerprint>?,
        signature: SignatureAttachment?,
    ): ZetesEnrolment {
        val zetesEnrolment = ZetesEnrolment.newInstance()
            .request {
                id(enrolment.enrolmentId.value)
                date(enrolment.completedAt)
                operator("DAT01")
                cnamAgentCode("DAT01")
                enrolmentKitId("SNEDAI")
                enrolmentSiteCode(enrolmentSite)
                enrolmentSite(enrolmentSite)
            }
            .personalInformation(enrolment.enrolmentDetails.personalDetails, enrolment.enrolmentDetails.contactDetails)
            .birth(enrolment.enrolmentDetails.birthDetails)
            .familyStatus(enrolment.enrolmentDetails.personalDetails.maritalStatus.code.value)
            .personType(enrolment.enrolmentDetails.professionalDetails.personCategory.label)
            .personTypeCode(enrolment.enrolmentDetails.professionalDetails.personCategory.code.value)
            .identificationDocument(enrolment.identityDocument)
            .address(enrolment.enrolmentDetails.address)
            .professionalInformation(enrolment.enrolmentDetails.professionalDetails)
            .subscription(enrolment.enrolmentDetails.subscriptionDetails)

        if (fingerprints != null) {
            zetesEnrolment.fingerprints(fingerprints)
        }

        zetesEnrolment.photo(Base64.encodeToString(photo.image))

        if (signature != null) {
            zetesEnrolment.signature(Base64.encodeToString(signature.image))
        }

        val numberOfScannedPages = enrolment.identityDocument.pageCount +
                enrolment.supportingDocuments.sumOf { it.pageCount }
        zetesEnrolment.numberOfScannedDocuments(numberOfScannedPages)

        return zetesEnrolment
    }
}
