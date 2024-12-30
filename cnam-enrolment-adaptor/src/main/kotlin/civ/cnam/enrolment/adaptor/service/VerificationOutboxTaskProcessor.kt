package civ.cnam.enrolment.adaptor.service

import civ.cnam.enrolment.adaptor.model.entity.VerificationOutboxTaskEntity
import civ.cnam.enrolment.adaptor.model.type.VerificationOutboxTaskProcessed
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.VerificationError.VerifierNotFound
import civ.cnam.enrolment.domain.error.VerificationError.VerifierVerificationFailed
import civ.cnam.enrolment.domain.model.query.enrolment.GetCompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.service.verifier.EnrolmentVerifier
import dev.dry.common.exception.CodedException
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.outbox.OutboxErrors
import dev.dry.core.outbox.OutboxItemProcessor
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Instance
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
])
class VerificationOutboxTaskProcessor(
    verifiers: Instance<EnrolmentVerifier>,
    private val getCompletedEnrolment: GetCompletedEnrolment,
) : OutboxItemProcessor<VerificationOutboxTaskEntity, VerificationOutboxTaskProcessed> {
    private val verifierByType = verifiers.associateBy { it.type }

    override fun process(
        item: VerificationOutboxTaskEntity
    ): Either<OutboxErrors.OutboxProcessingError, VerificationOutboxTaskProcessed> {
        val verifier = verifierByType[item.type]
        return if (verifier != null) {
            getCompletedEnrolment(item.enrolmentId)
                .fold({ left(OutboxErrors.wrap(it)) }, { verify(verifier, it) })
        } else {
            logger.info("verifier not found for verification outbox task with type '${item.type}")
            left(VerifierNotFound(item.type))
        }
    }

    private fun verify(
        verifier: EnrolmentVerifier,
        enrolment: CompletedEnrolment
    ): Either<OutboxErrors.OutboxProcessingError, VerificationOutboxTaskProcessed> {
        return try {
            verifier.verify(enrolment).fold(
                { left(OutboxErrors.wrap(it)) },
                { right(VerificationOutboxTaskProcessed()) }
            )
        } catch (ex: CodedException) {
            left(OutboxErrors.wrap(ex.error))
        } catch (ex: Exception) {
            left(VerifierVerificationFailed(ex))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VerificationOutboxTaskProcessor::class.java)
    }
}
