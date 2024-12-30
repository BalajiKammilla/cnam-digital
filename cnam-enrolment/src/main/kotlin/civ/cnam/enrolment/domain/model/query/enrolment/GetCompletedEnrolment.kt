package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.error.CompleteEnrolmentError
import civ.cnam.enrolment.domain.error.CompleteEnrolmentError.EnrolmentNotCompletedError
import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.common.function.flatMap
import dev.dry.core.data.model.value.MobileNumber
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetCompletedEnrolment(
    private val enrolmentRepository: EnrolmentRepository,
    private val mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
) {
    @Transactional
    operator fun invoke(id: PartialEnrolment.ID): Either<CompleteEnrolmentError, CompletedEnrolment> {
        logger.info("retrieving completed enrolment '$id'")
        return enrolmentRepository.findPartialEnrolment(id)
            .flatMap(::invoke)
    }

    @Transactional
    operator fun invoke(enrolmentId: EnrolmentId): Either<CompleteEnrolmentError, CompletedEnrolment> {
        logger.info("retrieving completed enrolment '$enrolmentId'")
        return enrolmentRepository.findPartialEnrolment(enrolmentId)
            .flatMap(::invoke)
    }

    @Transactional
    operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber
    ): Either<CompleteEnrolmentError, CompletedEnrolment> {
        logger.info("retrieving completed enrolment '$enrolmentId' for mobileNumber '$mobileNumber'")
        return enrolmentRepository.findPartialEnrolment(enrolmentId, mobileNumber)
            .flatMap(::invoke)
    }

    @Transactional
    operator fun invoke(enrolment: PartialEnrolment): Either<EnrolmentNotCompletedError, CompletedEnrolment> {
        return mapPartialToCompletedEnrolment(enrolment, enrolment.completedAt)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GetCompletedEnrolment::class.java)
    }
}
