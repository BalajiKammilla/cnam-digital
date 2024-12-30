package civ.cnam.api.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.dry.alert.domain.model.entity.Alert
import dev.dry.alert.domain.model.type.AlertType
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.alert.domain.model.value.AlertPriority
import java.time.LocalDateTime

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = AlertAdhocData::class, name = "ADHOC"),
    JsonSubTypes.Type(value = AlertTemplatedData::class, name = "TEMPLATED"),
])
interface AlertData {
    val type: AlertType
    val id: Long
    val batchId: Long
    val createdAt: LocalDateTime
    val reference: String?
    val secondaryReference: String?
    val reason: String?
    val sender: String
    val recipient: String
    val channel: AlertChannel
    val priority: AlertPriority
    val expiresAt: LocalDateTime?
    val retainUntil: LocalDateTime?
    val processedAt: LocalDateTime?
    val sendStatus: Alert.SendStatus?
    val readStatus: Alert.ReadStatus?
    val readStatusUpdatedAt: LocalDateTime?
}

class AlertAdhocData(
    override val id: Long,
    override val batchId: Long,
    override val createdAt: LocalDateTime,
    override val reference: String?,
    override val secondaryReference: String?,
    override val reason: String?,
    override val sender: String,
    override val recipient: String,
    override val channel: AlertChannel,
    override val priority: AlertPriority,
    override val expiresAt: LocalDateTime?,
    override val retainUntil: LocalDateTime?,
    override val processedAt: LocalDateTime?,
    override val sendStatus: Alert.SendStatus?,
    override val readStatus: Alert.ReadStatus?,
    override val readStatusUpdatedAt: LocalDateTime?,
    val body: String?,
    val subject: String?,
) : AlertData {
    override val type: AlertType = AlertType.ADHOC
}

class AlertTemplatedData(
    override val id: Long,
    override val batchId: Long,
    override val createdAt: LocalDateTime,
    override val reference: String?,
    override val secondaryReference: String?,
    override val reason: String?,
    override val sender: String,
    override val recipient: String,
    override val channel: AlertChannel,
    override val priority: AlertPriority,
    override val expiresAt: LocalDateTime?,
    override val retainUntil: LocalDateTime?,
    override val processedAt: LocalDateTime?,
    override val sendStatus: Alert.SendStatus?,
    override val readStatus: Alert.ReadStatus?,
    override val readStatusUpdatedAt: LocalDateTime?,
    val language: String?
) : AlertData {
    override val type: AlertType = AlertType.TEMPLATED
}
