package civ.cnam.enrolment.adaptor.configuration

import civ.cnam.enrolment.adaptor.service.EnrolmentOutboxProcessor
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_ENROLMENT_OUTBOX_JOB_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_ENROLMENT_OUTBOX_JOB_NAME
import dev.dry.core.scheduling.ScheduledJob
import dev.dry.core.scheduling.ScheduledJobFactory
import io.quarkus.arc.lookup.LookupIfProperty
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentOutboxProcessorScheduledJobConfiguration(
    private val outboxProcessor: EnrolmentOutboxProcessor,
    private val scheduledJobFactory: ScheduledJobFactory
) {
    @Produces
    @Singleton
    @LookupIfProperty(name = CNAM_ENROLMENT_OUTBOX_JOB_ENABLED, stringValue = "true")
    fun enrolmentOutboxProcessorScheduledJob(): ScheduledJob {
        return scheduledJobFactory.createFromConfigOrNull(CNAM_ENROLMENT_OUTBOX_JOB_NAME) {
            outboxProcessor.process()
        } ?: throw IllegalStateException("failed to create scheduled job from ") // TODO("report specific error")
    }
}