package civ.cnam.zetes.adaptor.configuration

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_ENROLMENT_PROCESSOR_ADAPTOR
import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.model.query.attachment.GetAttachment
import civ.cnam.enrolment.domain.model.repository.EnrolmentOutboxTaskRepository
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.zetes.adaptor.service.ZetesEnrolmentProcessor
import civ.cnam.zetes.api.client.ZetesApiClient
import civ.cnam.zetes.api.client.function.ApplyEncryption
import civ.cnam.zetes.api.client.function.MapDocumentPage
import civ.cnam.zetes.api.client.function.MapEnrolment
import civ.cnam.zetes.api.client.function.PrepareEnrolmentRequestEnvelope
import dev.dry.common.exception.SystemRuntimeException
import dev.dry.common.io.resource.Resource
import dev.dry.common.security.crypto.X509CertificateLoader
import dev.dry.common.time.TimeProvider
import io.quarkus.arc.lookup.LookupIfProperty
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import java.security.Security
import java.security.cert.X509Certificate

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class ZetesConfiguration(
    @ConfigProperty(name = BASE_URI)
    baseUri: String,
    @ConfigProperty(name = ENCRYPTION_CERTIFICATE_URI)
    encryptionCertificateUri: String,
) {
    private val baseUri: URI = URI(baseUri)
    private val encryptionCertificateResource: Resource = Resource.from(encryptionCertificateUri)

    @Produces
    @Singleton
    @LookupIfProperty(name = CNAM_ENROLMENT_PROCESSOR_ADAPTOR, stringValue = "zetes")
    fun enrolmentProcessor(
        timeProvider: TimeProvider,
        enrolmentRepository: EnrolmentRepository,
        enrolmentOutboxTaskRepository: EnrolmentOutboxTaskRepository,
        mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
        getAttachment: GetAttachment,
    ): ZetesEnrolmentProcessor {
        Security.addProvider(BouncyCastleProvider())
        val certificate = loadCertificate(encryptionCertificateResource)
        val applyEncryption = ApplyEncryption(certificate)
        val apiClient = ZetesApiClient(baseUri, applyEncryption)
        return ZetesEnrolmentProcessor(
            timeProvider,
            enrolmentRepository,
            enrolmentOutboxTaskRepository,
            mapPartialToCompletedEnrolment,
            getAttachment,
            apiClient,
            MapEnrolment(),
            PrepareEnrolmentRequestEnvelope(applyEncryption),
            MapDocumentPage(applyEncryption)
        )
    }

    private fun loadCertificate(resource: Resource): X509Certificate {
        return resource.openStream()?.use(X509CertificateLoader::load)
            ?: throw SystemRuntimeException("failed to load zetes API client certificate")
    }

    companion object {
        const val BASE_URI = "cnam.zetes.base-uri"
        const val ENCRYPTION_CERTIFICATE_URI = "cnam.zetes.encryption-certificate-uri"
    }
}