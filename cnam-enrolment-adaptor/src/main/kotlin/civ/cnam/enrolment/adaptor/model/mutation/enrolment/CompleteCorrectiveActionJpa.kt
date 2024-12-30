package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.CorrectiveActionEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.mutation.enrolment.CompleteCorrectiveAction
import civ.cnam.enrolment.domain.model.mutation.enrolment.CompleteCorrectiveAction.CorrectiveActionNotFound
import dev.dry.alert.domain.model.entity.Alert
import dev.dry.alert.domain.model.entity.Alert.ReadStatus
import dev.dry.alert.domain.model.mutation.UpdateAlertReadStatus
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.time.TimeProvider
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CompleteCorrectiveActionJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val updateEnrolmentCorrectiveActionPendingCount: UpdateEnrolmentCorrectiveActionPendingCount,
    private val updateAlertReadStatus: UpdateAlertReadStatus,
) : CompleteCorrectiveAction {
    override operator fun invoke(
        enrolmentId: PartialEnrolment.ID,
        type: CorrectiveActionType
    ): Either<CorrectiveActionNotFound, Unit> {
        logger.info("completing corrective action of type '$type' for enrolment '$enrolmentId'")
        val correctiveAction = findCorrectiveAction(enrolmentId, type)
            .fold({ return left(it) }, { it })

        return completeCorrectiveAction(correctiveAction)
    }

    private fun findCorrectiveAction(
        enrolmentId: PartialEnrolment.ID,
        type: CorrectiveActionType
    ): Either<CorrectiveActionNotFound, CorrectiveActionEntity> {
        val correctiveAction = jpaOperations.querySingleResultOrNull(
            CorrectiveActionEntity::class,
            FIND_CORRECTIVE_ACTION_BY_ENROLMENT_ID_AND_TYPE
        ) {
            parameter("enrolmentId", enrolmentId.value)
            parameter("type", type)
            parameter("status", CorrectiveActionStatus.PENDING)
        } ?: return correctiveActionNotFound(enrolmentId, type)
        return right(correctiveAction)
    }

    override operator fun invoke(id: CorrectiveAction.ID): Either<CorrectiveActionNotFound, Unit> {
        logger.info("completing corrective action '$id'")
        val correctiveAction = findCorrectiveAction(id)
            .fold({ return left(it) }, { it })

        return completeCorrectiveAction(correctiveAction)
    }

    private fun findCorrectiveAction(id: CorrectiveAction.ID): Either<CorrectiveActionNotFound, CorrectiveActionEntity> {
        val correctiveAction = jpaOperations.querySingleResultOrNull(
            CorrectiveActionEntity::class,
            FIND_CORRECTIVE_ACTION_BY_ID
        ) {
            parameter("id", id.value)
        } ?: return correctiveActionNotFound(id)
        return right(correctiveAction)
    }

    private fun completeCorrectiveAction(correctiveAction: CorrectiveActionEntity): Either.Right<Unit> {
        correctiveAction.status = CorrectiveActionStatus.COMPLETED
        correctiveAction.completedAt = timeProvider.now()
        jpaOperations.persist(correctiveAction)

        val alertId = correctiveAction.alertId
        if (alertId != Alert.ID.NULL) {
            logger.info("updating read status of alert '$alertId'")
            updateAlertReadStatus(alertId, ReadStatus.READ)
                .fold(
                    { logger.warn("update of alert read status failed with error -- $it") },
                    { logger.warn("update of alert read status completed") }
                )
        }

        val enrolment = correctiveAction.enrolment

        updateEnrolmentCorrectiveActionPendingCount(enrolment, -1)

        logger.info("updated corrective action")

        return right(Unit)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CompleteCorrectiveActionJpa::class.java)

        private const val FIND_CORRECTIVE_ACTION_BY_ID = "SELECT ca FROM CorrectiveAction ca WHERE ca.id = :id"

        private const val FIND_CORRECTIVE_ACTION_BY_ENROLMENT_ID_AND_TYPE = """
            SELECT ca FROM CorrectiveAction ca 
            WHERE ca.enrolmentId = :enrolmentId 
            AND ca.type = :type
            AND ca.status = :status
        """

        private fun correctiveActionNotFound(id: CorrectiveAction.ID): Either.Left<CorrectiveActionNotFound> {
            logger.info("corrective action not found with '$id'")
            return left(CorrectiveActionNotFound)
        }

        private fun correctiveActionNotFound(
            enrolmentId: PartialEnrolment.ID,
            type: CorrectiveActionType
        ): Either.Left<CorrectiveActionNotFound> {
            logger.info("corrective action not found with type '$type' for enrolment '$enrolmentId'")
            return left(CorrectiveActionNotFound)
        }
    }
}
