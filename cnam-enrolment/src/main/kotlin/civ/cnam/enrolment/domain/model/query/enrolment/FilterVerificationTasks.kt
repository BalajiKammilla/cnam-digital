package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask
import civ.cnam.enrolment.domain.model.type.enrolment.VerificationTaskFilter
import dev.dry.core.data.pagination.Page

interface FilterVerificationTasks {
    operator fun invoke(pageNumber: Int, pageSize: Int, filter: VerificationTaskFilter): Page<VerificationOutboxTask>
}
