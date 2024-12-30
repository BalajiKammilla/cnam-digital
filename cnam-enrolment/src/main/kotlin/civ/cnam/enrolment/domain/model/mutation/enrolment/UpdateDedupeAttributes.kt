package civ.cnam.enrolment.domain.model.mutation.enrolment

import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeMatchAttributes
import dev.dry.common.function.Either

interface UpdateDedupeAttributes {
    operator fun invoke(
        id: PartialEnrolment.ID,
        dedupeAttributes: DedupeMatchAttributes,
    ): Either<EnrolmentNotFound, Unit>
}