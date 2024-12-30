package civ.cnam.enrolment.domain.model.type.enrolment

import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.EnrolmentDetails
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import dev.dry.core.qrcode.QRCodeImage
import java.time.LocalDate
import java.time.LocalDateTime

class EnrolmentReceipt(
    val enrolmentId: EnrolmentId,
    val enrolmentDate: LocalDateTime,
    val firstNames: FirstName,
    val lastName: LastName,
    val nationalityLabel: String,
    val dateOfBirth: LocalDate,
    val residenceSubPrefectureLabel: String,
    val photoImage: ByteArray,
    val qrCodeImage: ByteArray,
) {
    companion object {
        fun from(
            completedEnrolment: CompletedEnrolment,
            details: EnrolmentDetails,
            photo: ByteArray,
            qrcodeImage: QRCodeImage
        ): EnrolmentReceipt {
            return EnrolmentReceipt(
                enrolmentId = completedEnrolment.enrolmentId,
                enrolmentDate = completedEnrolment.startedAt,
                firstNames = details.personalDetails.firstNames,
                lastName = details.personalDetails.lastName,
                nationalityLabel = details.personalDetails.currentNationality.label,
                dateOfBirth = details.birthDetails.date,
                residenceSubPrefectureLabel = details.address.subPrefecture.label,
                photoImage = photo,
                qrCodeImage = qrcodeImage.bytes,
            )
        }
    }
}