package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentActionEntity
import civ.cnam.enrolment.adaptor.model.entity.EnrolmentDeviceSessionEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import dev.dry.common.time.TimeProvider
import dev.dry.core.jaxrs.request.RequestScopedTraceId
import dev.dry.core.jpa.operations.JpaOperations
import dev.dry.core.security.auth.model.entity.DeviceSession
import dev.dry.core.security.auth.service.DeviceSessionIdSupplier
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CreateEnrolmentAction(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val timeProvider: TimeProvider,
    private val deviceSessionIdSupplier: DeviceSessionIdSupplier,
    private val requestScopedTraceId: RequestScopedTraceId,
) {
    operator fun invoke(
        kind: EnrolmentActionKind,
        enrolment: PartialEnrolmentEntity,
        attachmentId: String? = null,
    ) {
        val deviceSession = findEnrolmentDeviceSession()
        val action = EnrolmentActionEntity(
            kind = kind,
            createdAt = timeProvider.now(),
            enrolment = enrolment,
            deviceSession = deviceSession,
            attachmentId = attachmentId,
            traceId = requestScopedTraceId.traceId,
        )
        jpaOperations.persist(action)
    }

    private fun findEnrolmentDeviceSession(): EnrolmentDeviceSessionEntity? {
        return deviceSessionIdSupplier.getDeviceSessionId()
            .map(::findEnrolmentDeviceSession)
            .orElse(null)
    }

    private fun findEnrolmentDeviceSession(deviceSessionId: DeviceSession.ID): EnrolmentDeviceSessionEntity? {
        val jpql = "SELECT ds FROM EnrolmentDeviceSession ds WHERE ds.id = :deviceSessionId"
        return jpaOperations.querySingleResultOrNull(EnrolmentDeviceSessionEntity::class, jpql) {
            parameter("deviceSessionId", deviceSessionId.value)
        }
    }
}
