package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.CorrectiveAction
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.alert.domain.model.entity.Alert
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "CorrectiveAction")
class CorrectiveActionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override val id: CorrectiveAction.ID,

    @Column(nullable = false, updatable = false)
    override val type: CorrectiveAction.CorrectiveActionType,

    @Column(nullable = false, updatable = false)
    override val createdAt: LocalDateTime,

    @Column(nullable = false)
    override var status: CorrectiveAction.CorrectiveActionStatus,

    @Column(insertable = false)
    override var completedAt: LocalDateTime?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = ENROLMENT_ID,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_corrective_action_enrolment"),
    )
    @JsonIgnore
    val enrolment: PartialEnrolmentEntity,

    @Column(name = ENROLMENT_ID, nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    val enrolmentId: PartialEnrolment.ID,

    @Column(nullable = false, updatable = false)
    @JsonIgnore
    val alertId: Alert.ID,
) : CorrectiveAction {
    constructor(
        type: CorrectiveAction.CorrectiveActionType,
        createdAt: LocalDateTime,
        enrolment: PartialEnrolmentEntity,
        alertId: Alert.ID,
    ): this(
        id = CorrectiveAction.ID.NULL,
        type = type,
        createdAt = createdAt,
        status = CorrectiveAction.CorrectiveActionStatus.PENDING,
        completedAt = null,
        enrolment = enrolment,
        enrolmentId = enrolment.id,
        alertId = alertId,
    )

    companion object {
        const val ENROLMENT_ID = "enrolment_id"
    }
}