package civ.cnam.api.steps.enrolment

import civ.cnam.api.context.EnrolmentContext
import civ.cnam.api.context.ScenarioContext
import civ.cnam.api.resource.Resources
import civ.cnam.enrolment.domain.model.type.attachment.FingerprintImage
import io.cucumber.java.en.Then
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Fail.fail

class FingerprintsImageSteps {
    val ctx = ScenarioContext
    val enrolment = EnrolmentContext


    @Then("I fetch the fingerprint images")
    fun I_fetch_the_fingerprint_images() {
        ctx.lastResponse = Resources.fingerprintImages.getFingerprintsImage(
            enrolmentId = enrolment.enrolmentId,
            attachmentId = enrolment.fingerprintAttachmentId)
    }

    @Then("retrieved the fingerprint images successfully")
    fun retrieved_the_fingerprint_images_successfully() {
        val responses = ctx.lastResponse ?: fail("LastResponse is null")
        assertThat(responses.statusCode).isEqualTo(200)
        val jsonPath = responses.body.jsonPath()
        val fingerprintsImage = jsonPath.getString("data.image")
        assertThat(fingerprintsImage).isNotNull
        val fingerprints = jsonPath.getList("data", FingerprintImage::class.java)

        fingerprints.forEach { fingerprint ->
            if (fingerprint != null) {
                if (fingerprint.fingerprintConversionSucceeded) {
                    assertThat(fingerprint.image).isNotNull
                } else {
                    assertThat(fingerprint.image).isNull()
                }
            } else {
                fail("Fingerprint is null")
            }
        }
        assertThat(fingerprints).hasSize(10)
    }
}
