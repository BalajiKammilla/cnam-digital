package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.EnrolmentAction
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.tracing.TraceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "EnrolmentAction")
class EnrolmentActionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: Long,
    @Column(nullable = false, updatable = false)
    @Enumerated
    override val kind: EnrolmentActionKind,
    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = ENROLMENT_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_action_enrolment"),
    )
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = DEVICE_SESSION_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_action_device_session"),
    )
    @JsonIgnore
    override val deviceSession: EnrolmentDeviceSessionEntity?,
    @Column(updatable = false)
    override val attachmentId: String?,
    @Column(nullable = false, updatable = false)
    override val traceId: TraceId
) : EnrolmentAction {
    constructor(
        kind: EnrolmentActionKind,
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity,
        deviceSession: EnrolmentDeviceSessionEntity?,
        attachmentId: String?,
        traceId: TraceId
    ): this(
        id = 0,
        kind = kind,
        createdAt = createdAt,
        enrolment = enrolment,
        deviceSession = deviceSession,
        attachmentId = attachmentId,
        traceId = traceId,
    )

    companion object {
        const val ENROLMENT_ID = "enrolment_id"
        const val DEVICE_SESSION_ID = "device_session_id"
    }
}
