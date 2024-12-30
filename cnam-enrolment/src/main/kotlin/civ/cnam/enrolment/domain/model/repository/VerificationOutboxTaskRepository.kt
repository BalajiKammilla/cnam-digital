package civ.cnam.enrolment.domain.model.repository

import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either

interface VerificationOutboxTaskRepository {
    fun createTasks(enrolmentId: EnrolmentId, types: Set<VerificationType>): Either<EnrolmentNotFound, Unit>
}