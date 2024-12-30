package civ.cnam.enrolment.adaptor.service

import civ.cnam.enrolment.adaptor.model.entity.VerificationOutboxTaskEntity
import civ.cnam.enrolment.adaptor.model.repository.VerificationOutboxTaskRepositoryJpa
import civ.cnam.enrolment.adaptor.model.type.VerificationOutboxTaskProcessed
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.outbox.OutboxProcessor
import dev.dry.core.tracing.Tracer
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
])
class VerificationOutboxProcessor(
    taskRepository: VerificationOutboxTaskRepositoryJpa,
    taskrPocessor: VerificationOutboxTaskProcessor,
    tracer: Tracer,
) : OutboxProcessor<
        VerificationOutboxTask.ID,
        VerificationOutboxTaskEntity,
        VerificationOutboxTaskEntity,
        VerificationOutboxTaskProcessed
>(
    "verification-task",
    batchSize = 1000,
    itemRepository = taskRepository,
    itemTransformer = { right(it) },
    itemProcessor = taskrPocessor,
    tracer = tracer,
)