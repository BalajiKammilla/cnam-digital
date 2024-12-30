package civ.cnam.enrolment.adaptor.service

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask
import civ.cnam.enrolment.domain.model.repository.EnrolmentOutboxTaskRepository
import civ.cnam.enrolment.domain.service.EnrolmentProcessor
import dev.dry.common.exception.Exceptions
import dev.dry.core.tracing.Tracer
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Instance
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentOutboxProcessor(
    enrolmentProcessorInstance: Instance<EnrolmentProcessor>,
    private val enrolmentOutboxTaskRepository: EnrolmentOutboxTaskRepository,
    private val tracer: Tracer,
) {
    private val enrolmentProcessor: EnrolmentProcessor = enrolmentProcessorInstance.get()

    fun process() {
        try {
            enrolmentOutboxTaskRepository.findPendingTasks(pageNumber = 1, pageSize = 10)
                .content
                .forEach(::process)
        } catch (th: Throwable) {
            logger.error("error executing scheduled enrolment processing -- {}", Exceptions.getMessageChain(th))
        }
    }

    private fun process(enrolmentOutboxTaskId: EnrolmentOutboxTask.ID) {
        tracer.trace {
            val processingId = it.value
            val task = enrolmentOutboxTaskRepository.findAndUpdatePendingTaskForProcessing(enrolmentOutboxTaskId, processingId)
            if (task != null) {
                enrolmentProcessor.process(task, processingId)
            } else {
                logger.info("failed to update pending enrolment outbox task - '$enrolmentOutboxTaskId")
            }
        }
    }

    private val logger = LoggerFactory.getLogger(EnrolmentOutboxProcessor::class.java)
}
