package civ.cnam.api.resource.enrolment

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class FingerprintsImageResource(
    private val resourceUri: String,
    accessToken: String) : AbstractAuthenticatedResource(accessToken) {

    constructor(accessToken: String) : this("/api/management/enrolment",accessToken)

    fun getFingerprintsImage(
        enrolmentId: String?,
        attachmentId: String?
    ): Response {
        return get("${enrolmentId}/fingerprints/${attachmentId}")
    }

    private fun get(uriPath: String): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri/$uriPath")
        return spec.andReturn()
    }
}
