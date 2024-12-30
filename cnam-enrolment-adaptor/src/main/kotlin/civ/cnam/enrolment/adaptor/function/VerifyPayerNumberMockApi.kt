package civ.cnam.enrolment.adaptor.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_PAYER_VERIFICATION_ADAPTOR
import civ.cnam.enrolment.domain.function.VerifyPayerNumber
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import dev.dry.core.http.client.ApiClient
import dev.dry.core.http.client.MultipartFormData
import dev.dry.core.http.client.decprator.RequestDecorator
import dev.dry.core.http.client.interceptor.Interceptors
import dev.dry.core.http.client.interceptor.TimingInterceptor
import dev.dry.core.serialization.ObjectReader
import dev.dry.core.serialization.ObjectWriter
import io.quarkus.arc.lookup.LookupIfProperty
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import java.net.http.HttpRequest

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
@LookupIfProperty(name = CNAM_PAYER_VERIFICATION_ADAPTOR, stringValue = "mock-api")
class VerifyPayerNumberMockApi(
    @ConfigProperty(name = "cnam.payer.verification.api.base-uri")
    baseUri: String,
    @ConfigProperty(name = "cnam.payer.verification.api.username")
    private val username: String,
    @ConfigProperty(name = "cnam.payer.verification.api.password")
    private val password: String,
    @ConfigProperty(name = "cnam.payer.verification.api.request-response-logging.enabled", defaultValue = "true")
    private val enableRequestAndResponseLogging: Boolean,
    objectReader: ObjectReader,
    objectWriter: ObjectWriter,
) : VerifyPayerNumber {
    private val client = ApiClient(
        name = "CNAM Number Verification",
        baseUri = baseUri,
        commonInterceptor = Interceptors(TimingInterceptor()),
        objectReader = objectReader,
        objectWriter = objectWriter,
        enableRequestAndResponseLogging = enableRequestAndResponseLogging,
        connectionTimeoutSeconds = 15
    )
    private val getTokenFormData = MultipartFormData.builder()
        .part("email", username)
        .part("secret", password)
        .build()
    private val authenticationRequestDecorator = object : RequestDecorator {
        override fun decorate(builder: HttpRequest.Builder) {
            val response = client.post("/get-token/", getTokenFormData, GetTokenResponse::class.java)
            builder.header("Authorization", "Bearer ${response.token}")
        }
    }

    override fun invoke(number: CNAMNumber): Boolean {
        logger.info("verifying payer number")
        val getVerification = MultipartFormData.builder()
            .part("numero_secu", number.value)
            .build()
        val response = client.post(
            "/populations/enrolements/",
            getVerification,
            GetVerificationResponse::class.java,
            authenticationRequestDecorator
        )
        logger.info("verifying payer number completed - $response")
        return response.success
    }

    class GetTokenResponse(val success: Boolean, val token: String)
    class GetVerificationResponse(val success: Boolean, val numero_recepisse: String, val date_enrolement: String) {
        override fun toString(): String =
            "success: $success, numero_recepisse: $numero_recepisse, date_enrolement: $date_enrolement"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(VerifyPayerNumberMockApi::class.java)
    }
}
