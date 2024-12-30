package civ.cnam.zetes.api.client.function

import org.bouncycastle.cms.CMSAlgorithm
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.security.cert.X509Certificate

class ApplyEncryption(private val certificate: X509Certificate) {
    private val logger = LoggerFactory.getLogger(ApplyEncryption::class.java)

    operator fun invoke(contentBytes: ByteArray): ByteArray {
        logger.info("applying encryption to ${contentBytes.size} bytes")
        val gen = CMSEnvelopedDataStreamGenerator()
        gen.addRecipientInfoGenerator(JceKeyTransRecipientInfoGenerator(certificate))

        val encryptor = JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .build()

        val outputStream = ByteArrayOutputStream()
        gen.open(outputStream, encryptor).use { encryptingStream ->
            encryptingStream.write(contentBytes)
        }

        return outputStream.toByteArray()
    }

    operator fun invoke(content: String): ByteArray = invoke(content.toByteArray(Charsets.UTF_8))
}