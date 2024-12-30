package civ.cnam.enrolment.domain.function

import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentQRCodeContent
import dev.dry.core.qrcode.QRCodeContent
import dev.dry.core.qrcode.QRCodeGenerator
import dev.dry.core.qrcode.QRCodeImage

class GenerateEnrolmentQRCode(private val qrcodeGenerator: QRCodeGenerator) {
    companion object {
        const val ROOT_TAG = "CNAM"
    }

    operator fun invoke(content: EnrolmentQRCodeContent): QRCodeImage {
        return qrcodeGenerator.generate(encode(content))
    }

    private fun encode(content: EnrolmentQRCodeContent): QRCodeContent {
        return QRCodeContent.builder(ROOT_TAG)
            .tag("EID", content.enrolmentId)
            .tag("DOE", content.enrolmentCompletedAt)
            .tag("TIT", content.title.label)
            .tag("FIN", content.firstNames.value)
            .tag("LAN", content.lastName.value)
            .tag("DOB", content.dateOfBirth)
            .tag("LOB", content.locationOfBirth?.label ?: content.countryOfBirth.label)
            .tag("PHN", content.mobileNumber.value)
            .tag("ADR", content.residenceAddress.subPrefecture.label)
            .tag("PRF", content.profession?.label ?: "")
            .tag("CAT", content.personCategory.label)
            .tag("NAT", content.nationality.label)
            //.tag("DON", content.identificationDocument.documentNumber.value) // TODO("fix this")
            //.tag("DOT", content.identificationDocument.documentTypeCode.value)
            .build()
    }
}
