package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.model.entity.DedupeMatch
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either

interface CreateOrUpdateDedupeMatch {
    operator fun invoke(completedEnrolment: CompletedEnrolment): Either<CodedError, DedupeMatch?>
}