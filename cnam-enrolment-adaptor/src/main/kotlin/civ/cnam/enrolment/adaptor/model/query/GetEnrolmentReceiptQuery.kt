package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.function.GenerateEnrolmentQRCode
import civ.cnam.enrolment.domain.model.query.attachment.GetAttachment
import civ.cnam.enrolment.domain.model.query.enrolment.GetCompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentQRCodeContent
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentReceipt
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.qrcode.QRCodeGenerator
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetEnrolmentReceiptQuery(
    private val getCompletedEnrolment: GetCompletedEnrolment,
    private val getAttachment: GetAttachment,
    qrCodeGenerator: QRCodeGenerator,
) {
    private val generateEnrolmentQRCode = GenerateEnrolmentQRCode(qrCodeGenerator)

    operator fun invoke(mobileNumber: MobileNumber, enrolmentId: EnrolmentId): Either<CodedError, EnrolmentReceipt> {
        return getCompletedEnrolment(enrolmentId, mobileNumber).map { completedEnrolment ->
            val photo = getAttachment(enrolmentId, completedEnrolment.photoAttachmentId)
            val details = completedEnrolment.enrolmentDetails
            // TODO("Fix this")
            val content = EnrolmentQRCodeContent.from(enrolmentId, completedEnrolment.completedAt, details)
            val qrcode = generateEnrolmentQRCode(content)
            EnrolmentReceipt.from(completedEnrolment, details, photo.image, qrcode)
        }
    }
}
