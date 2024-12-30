package civ.cnam.enrolment.adaptor.configuration

import civ.cnam.enrolment.adaptor.service.VerificationOutboxProcessor
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_VERIFICATION_OUTBOX_JOB_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_VERIFICATION_OUTBOX_JOB_NAME
import dev.dry.core.scheduling.ScheduledJob
import dev.dry.core.scheduling.ScheduledJobFactory
import io.quarkus.arc.lookup.LookupIfProperty
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class VerificationOutboxTaskScheduledJobConfiguration(
    private val verificationOutboxProcessor: VerificationOutboxProcessor,
    private val scheduledJobFactory: ScheduledJobFactory
) {
    @Produces
    @Singleton
    @LookupIfProperty(name = CNAM_VERIFICATION_OUTBOX_JOB_ENABLED, stringValue = "true")
    fun verificationOutboxTaskScheduledJob(): ScheduledJob {
        return scheduledJobFactory.createFromConfigOrNull(CNAM_VERIFICATION_OUTBOX_JOB_NAME) {
            verificationOutboxProcessor.process()
        } ?: throw IllegalStateException("failed to create scheduled job from ") // TODO("report specific error")
    }
}