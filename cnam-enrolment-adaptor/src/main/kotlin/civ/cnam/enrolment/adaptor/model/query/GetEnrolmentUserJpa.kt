package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentUserEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentUserNotFound
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.core.data.model.entity.User
import dev.dry.core.data.model.value.UserName
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetEnrolmentUserJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
) {
    operator fun <E : CodedError> invoke(
        userName: UserName,
        onNotFound: (UserName) -> E,
    ): Either<E, EnrolmentUserEntity> {
        val result = orNull(userName) ?: return Either.left(onNotFound(userName))
        return Either.right(result)
    }

    operator fun invoke(userName: UserName): Either<EnrolmentUserNotFound, EnrolmentUserEntity> {
        return invoke(userName) { EnrolmentUserNotFound }
    }

    fun orNull(userName: UserName): EnrolmentUserEntity? {
        return jpaOperations.querySingleResultOrNull(EnrolmentUserEntity::class, FIND_BY_USER_NAME_JPQL) {
            parameter("userName", userName.value)
        }
    }

    operator fun <E : CodedError> invoke(
        id: User.ID,
        onNotFound: (User.ID) -> E,
    ): Either<E, EnrolmentUserEntity> {
        val result = orNull(id) ?: return Either.left(onNotFound(id))
        return Either.right(result)
    }

    fun orNull(id: User.ID): EnrolmentUserEntity? {
        return jpaOperations.querySingleResultOrNull(EnrolmentUserEntity::class, FIND_BY_ID_JPQL) {
            parameter("id", id.value)
        }
    }

    companion object {
        const val FIND_BY_USER_NAME_JPQL = "SELECT eu FROM EnrolmentUser eu WHERE eu.userName = :userName"
        const val FIND_BY_ID_JPQL = "SELECT eu FROM EnrolmentUser eu WHERE eu.id = :id"
    }
}