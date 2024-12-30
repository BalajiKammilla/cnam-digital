package civ.cnam.enrolment.adaptor.service

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import dev.dry.alert.constants.AlertBuildProperty.DRY_ALERT_CHANNEL_SMS_ENABLED
import dev.dry.alert.constants.AlertBuildProperty.DRY_ALERT_ENABLED
import dev.dry.alert.domain.error.AlertErrors.SendAlertError
import dev.dry.alert.domain.error.AlertErrors.SendFailed
import dev.dry.alert.domain.error.AlertErrors.SendFailedWithRejection
import dev.dry.alert.domain.error.AlertErrors.SendFailedWithTransientError
import dev.dry.alert.domain.model.type.AlertMessage
import dev.dry.alert.domain.model.type.AlertMessageProcessed
import dev.dry.alert.domain.model.value.AlertChannel
import dev.dry.alert.domain.model.value.AlertMessageReference
import dev.dry.alert.domain.service.AlertChannelMessageSender
import dev.dry.common.function.Either
import dev.dry.core.http.client.ApiClient
import dev.dry.core.http.client.interceptor.Interceptors
import dev.dry.core.http.client.interceptor.TimingInterceptor
import dev.dry.core.serialization.ObjectReader
import dev.dry.core.serialization.ObjectWriter
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import java.io.IOException

@Singleton
@IfBuildProperty.List(value = [
    IfBuildProperty(name = DRY_ALERT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = DRY_ALERT_CHANNEL_SMS_ENABLED, stringValue = "true")
])
class SmsProAlertChannelMessageSender(
    @ConfigProperty(name = "cnam.sms.base-uri")
    baseUri: String,
    @ConfigProperty(name = "cnam.sms.code")
    private val code: String,
    @ConfigProperty(name = "cnam.sms.username")
    private val username: String,
    @ConfigProperty(name = "cnam.sms.password")
    private val password: String,
    @ConfigProperty(name = "cnam.sms.request-response-logging.enabled", defaultValue = "true")
    private val enableRequestAndResponseLogging: Boolean,
    objectReader: ObjectReader,
    objectWriter: ObjectWriter,
) : AlertChannelMessageSender {
    private val apiClient = ApiClient(
        name = "SMS Pro",
        baseUri = baseUri,
        commonInterceptor = Interceptors(TimingInterceptor(), ),
        objectReader = objectReader,
        objectWriter = objectWriter,
        enableRequestAndResponseLogging = enableRequestAndResponseLogging,
        connectionTimeoutSeconds = 5
    )

    init {
        logger.info("initialising SmsProAlertSender")
        logger.info("   - baseUri: '$baseUri'")
        logger.info("   - code: '$code'")
        logger.info("   - username: '$username'")
        logger.info("   - enableRequestAndResponseLogging: '$enableRequestAndResponseLogging'")
    }

    override val channel: AlertChannel get() = AlertChannel.SMS

    override fun send(alertMessage: AlertMessage): Either<SendAlertError, AlertMessageProcessed> {
        val sender = alertMessage.sender
        logger.info("sending smspro request with for recipient '${alertMessage.recipient}' from sender '$sender'")
        val request = SmsProRequest(
            code = code,
            username = username,
            password = password,
            sender = sender.value,
            body = alertMessage.body,
            recipient = alertMessage.recipient.value.replace("+", "")
        )
        val response = try {
            apiClient.post("/addOneSms", request, SmsProResponseBody::class.java, null)
                .data.firstOrNull()
        } catch (ex: IOException) {
            return Either.left(SendFailedWithTransientError(ex))
        } catch (th: Throwable) {
            return Either.left(SendFailed(th))
        }

        val status = response?.status
        val reference = response?.reference
        logger.info("received smspro response with status '${status}' and reference '${reference}'")
        return when(status) {
            "sms_received" -> Either.right(AlertMessageProcessed(reference))
            "sms_rejected" -> Either.left(SendFailedWithRejection(reference))
            else -> Either.right(AlertMessageProcessed(reference))
        }
    }

    class SmsProRequest(
        @JsonProperty("Code")
        val code: String,
        @JsonProperty("Username")
        val username: String,
        @JsonProperty("Password")
        val password: String,
        @JsonProperty("Sender")
        val sender: String,
        @JsonProperty("Sms")
        val body: String,
        @JsonProperty("Dest")
        val recipient: String
    )

    class SmsProResponse(
        @JsonAlias("Ref")
        reference: String?,
        @JsonAlias("Sender")
        val sender: String?,
        @JsonAlias("Dest")
        val recipient: String?,
        @JsonAlias("Sms")
        val body: String?,
        @JsonAlias("Cpt_sms")
        val cpt: Long,
        @JsonAlias("Statut")
        val status: String
    ) {
        val reference: AlertMessageReference? = if (reference != null) AlertMessageReference(reference) else null
    }

    class SmsProResponseBody(
        @JsonAlias("Rep")
        val data: List<SmsProResponse>
    )

    companion object {
        private val logger = LoggerFactory.getLogger(SmsProAlertChannelMessageSender::class.java)
    }
}
