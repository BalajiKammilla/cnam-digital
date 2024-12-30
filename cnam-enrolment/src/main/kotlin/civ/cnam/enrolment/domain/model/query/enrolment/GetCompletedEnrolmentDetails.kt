package civ.cnam.enrolment.domain.model.query.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.repository.EnrolmentUserRepository
import civ.cnam.enrolment.domain.model.type.EnrolmentUserData
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.flatten
import dev.dry.common.function.map
import dev.dry.core.data.model.type.DeviceAttributes
import dev.dry.user.domain.model.repository.DeviceRepository
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetCompletedEnrolmentDetails(
    private val enrolmentRepository: EnrolmentRepository,
    private val getCompletedEnrolment: GetCompletedEnrolment,
    private val enrolmentUserRepository: EnrolmentUserRepository,
    private val deviceRepository: DeviceRepository,
) {
    @Transactional
    operator fun invoke(enrolmentId: EnrolmentId): Either<CodedError, CompletedEnrolmentDetails> {
        return enrolmentRepository.findPartialEnrolment(enrolmentId)
            .map { enrolment ->
                getCompletedEnrolment(enrolment).map { completedEnrolment ->
                    val user = enrolmentUserRepository.findByMobileNumber(enrolment.mobileNumber)
                    val devices = enrolment.actions
                        .associateBy(
                            { it.deviceSession?.deviceAttributes?.deviceIdentifier },
                            { it.deviceSession?.deviceAttributes }
                        )
                        .values
                        .filterNotNull()
                    CompletedEnrolmentDetails(completedEnrolment, EnrolmentUserData(user), devices)
                }
            }.flatten()
    }

    class CompletedEnrolmentDetails(
        val enrolment: CompletedEnrolment,
        val enrolledBy: EnrolmentUserData,
        val enrolmentDevices: List<DeviceAttributes>,
    )
}