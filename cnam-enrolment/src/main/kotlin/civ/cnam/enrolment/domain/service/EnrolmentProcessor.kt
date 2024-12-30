package civ.cnam.enrolment.domain.service

import civ.cnam.enrolment.domain.model.entity.EnrolmentOutboxTask

fun interface EnrolmentProcessor {
    fun process(task: EnrolmentOutboxTask, processingId: String)
}
