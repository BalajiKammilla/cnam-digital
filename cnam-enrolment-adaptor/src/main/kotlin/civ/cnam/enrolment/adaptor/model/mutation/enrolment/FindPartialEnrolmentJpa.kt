package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFoundError
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFoundForMobileNumber
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class FindPartialEnrolmentJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) {
    operator fun <E : CodedError> invoke(
        enrolmentId: EnrolmentId,
        onNotFound: (EnrolmentId) -> E,
    ): Either<E, PartialEnrolmentEntity> {
        val result = orNull(enrolmentId) ?: return Either.left(onNotFound(enrolmentId))
        return Either.right(result)
    }

    operator fun invoke(enrolmentId: EnrolmentId): Either<EnrolmentNotFound, PartialEnrolmentEntity> {
        return invoke(enrolmentId) { EnrolmentNotFound }
    }

    operator fun <E : CodedError, NotFound : E, NotFoundWithMobileNumber : E> invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
        onNotFound: (EnrolmentId) -> NotFound,
        onNotFoundWithMobileNumber: (EnrolmentId, MobileNumber) -> NotFoundWithMobileNumber,
    ): Either<E, PartialEnrolmentEntity> {
        val result = orNull(enrolmentId) ?: return Either.left(onNotFound(enrolmentId))
        return if (result.mobileNumber != mobileNumber) {
            Either.left(onNotFoundWithMobileNumber(enrolmentId, mobileNumber))
        } else {
            Either.right(result)
        }
    }

    operator fun invoke(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
    ): Either<EnrolmentNotFoundError, PartialEnrolmentEntity> {
        return invoke(enrolmentId, mobileNumber, { EnrolmentNotFound }, { _, _ -> EnrolmentNotFoundForMobileNumber })
    }

    fun orNull(enrolmentId: EnrolmentId): PartialEnrolmentEntity? {
        return jpaOperations.querySingleResultOrNull(PartialEnrolmentEntity::class, FIND_BY_ENROLMENT_ID_JPQL) {
            parameter("enrolmentId", enrolmentId.value)
        }
    }

    fun orNull(id: PartialEnrolment.ID): PartialEnrolmentEntity? {
        return jpaOperations.querySingleResultOrNull(PartialEnrolmentEntity::class, FIND_BY_ID_JPQL) {
            parameter("id", id.value)
        }
    }

    operator fun <E : CodedError> invoke(
        id: PartialEnrolment.ID,
        onNotFound: (PartialEnrolment.ID) -> E,
    ): Either<E, PartialEnrolmentEntity> {
        val result = orNull(id) ?: return Either.left(onNotFound(id))
        return Either.right(result)
    }

    operator fun invoke(id: PartialEnrolment.ID): Either<EnrolmentNotFound, PartialEnrolmentEntity> {
        return invoke(id) { EnrolmentNotFound }
    }

    companion object {
        const val FIND_BY_ENROLMENT_ID_JPQL = "SELECT pe FROM PartialEnrolment pe WHERE pe.enrolmentId = :enrolmentId"
        const val FIND_BY_ID_JPQL = "SELECT pe FROM PartialEnrolment pe WHERE pe.id = :id"
    }
}