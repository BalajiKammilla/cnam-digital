package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.CorrectiveActionEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionType
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction.CreateCorrectiveActionError
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateCorrectiveAction.FailedToCreateAlert
import civ.cnam.enrolment.domain.model.type.enrolment.alert.CorrectiveActionRequiredAlert
import civ.cnam.enrolment.domain.model.type.enrolment.alert.CorrectiveActionRequiredAlert.CorrectiveActionReason
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.alert.domain.model.entity.Alert
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.alert.domain.service.AlertCreator
import dev.dry.alert.domain.service.AlertCreator.NewAlertsCreated
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.common.function.mapLeft
import dev.dry.common.time.TimeProvider
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CreateCorrectiveActionJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
    private val updateEnrolmentCorrectiveActionPendingCount: UpdateEnrolmentCorrectiveActionPendingCount,
    private val alertCreator: AlertCreator,
) : CreateCorrectiveAction {
    override fun invoke(
        enrolmentId: EnrolmentId,
        type: CorrectiveActionType
    ): Either<CreateCorrectiveActionError, Unit> {
        logger.info("creating corrective action of type '$type' for enrolment '$enrolmentId'")

        val enrolment = findPartialEnrolment(enrolmentId) { EnrolmentNotFound }
            .fold({ return left(it) }, { it })

        return createCorrectiveAction(enrolment, type)
    }

    override operator fun invoke(
        enrolmentId: PartialEnrolment.ID,
        type: CorrectiveActionType
    ): Either<CreateCorrectiveActionError, Unit> {
        logger.info("creating corrective action of type '$type' for enrolment '$enrolmentId'")

        val enrolment = findPartialEnrolment(enrolmentId) { EnrolmentNotFound }
            .fold({ return left(it) }, { it })

        return createCorrectiveAction(enrolment, type)
    }

    private fun createCorrectiveAction(
        enrolment: PartialEnrolmentEntity,
        type: CorrectiveActionType,
    ): Either<CreateCorrectiveActionError, Unit> {
        val alertId = createCorrectiveActionAlert(enrolment, type)
            .fold({ return left(it) }, ::findAppAlertId)

        val correctiveAction = CorrectiveActionEntity(
            type = type,
            createdAt = timeProvider.now(),
            enrolment = enrolment,
            alertId = alertId,
        )
        jpaOperations.persist(correctiveAction)

        updateEnrolmentCorrectiveActionPendingCount(enrolment, 1)

        return right(Unit)
    }

    private fun createCorrectiveActionAlert(
        enrolment: PartialEnrolmentEntity,
        type: CorrectiveActionType,
    ): Either<CreateCorrectiveActionError, NewAlertsCreated> {
        logger.info("creating corrective action alert for enrolment '${enrolment.id}'")
        val reason = when (type) {
            CorrectiveActionType.IDENTITY_DOCUMENT -> CorrectiveActionReason.IDENTITY_DOCUMENT_REJECTED
            CorrectiveActionType.SUBSCRIPTION_PAYER -> CorrectiveActionReason.SUBSCRIPTION_PAYER_NOT_VERIFIED
        }

        val alert = CorrectiveActionRequiredAlert.from(enrolment, reason)
            .fold({ return left(it) }, { it })

        return alertCreator.create(alert)
            .mapLeft {
                logger.error(
                    "failed to create corrective action alert of type '$type' for enrolment '${enrolment.enrolmentId}' - $it"
                )
                // TODO("rollback transaction")
                FailedToCreateAlert
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CreateCorrectiveActionJpa::class.java)

        fun findAppAlertId(created: NewAlertsCreated): Alert.ID {
            for (alert in created.alerts) {
                if (alert.channel == AlertChannel.NONE) {
                    return alert.id
                }
            }
            return Alert.ID.NULL
        }
    }
}
