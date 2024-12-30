package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.query.attachment.GetAttachment
import civ.cnam.enrolment.domain.model.query.attachment.GetFingerprintImages
import civ.cnam.enrolment.domain.model.type.attachment.Fingerprint
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintImage
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintsAttachment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import dev.dry.common.exception.Exceptions
import dev.dry.common.function.Either
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.jnbis.api.Jnbis
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetFingerprintsImageJpa(private val getAttachment: GetAttachment) : GetFingerprintImages {
    private val logger: Logger = LoggerFactory.getLogger(GetFingerprintsImageJpa::class.java)

    override fun invoke(
        enrolmentId: EnrolmentId,
        attachmentId: FingerprintsAttachmentId
    ): List<FingerprintImage> {
        val fingerprintAttachment: FingerprintsAttachment = getAttachment(enrolmentId, attachmentId)
        val fingerprints: List<Fingerprint> = fingerprintAttachment.fingerprints

        return fingerprints.map { fingerprint ->
            val result: Either<Unit, ByteArray> = convertToFingerprintImage(fingerprint.image)
            result.fold(
                { FingerprintImage(fingerprint.fingerIndex, null, false) },
                { FingerprintImage(fingerprint.fingerIndex, it, true) }
            )
        }
    }
    private fun convertToFingerprintImage(image: ByteArray): Either<Unit, ByteArray> {
        return try {
            val decodedImage = Jnbis.wsq()
                .decode(image)
                .toPng()
                .asByteArray()

            Either.Right(decodedImage)
        } catch (e: RuntimeException) {
            logger.error("Fingerprint WSQ conversion failed -- {}", Exceptions.getMessageChain(e))
            Either.Left(Unit)
        }
    }
}
