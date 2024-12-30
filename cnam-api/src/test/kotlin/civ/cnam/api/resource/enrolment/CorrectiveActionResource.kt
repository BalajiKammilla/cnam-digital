package civ.cnam.api.resource.enrolment

import civ.cnam.enrolment.domain.model.entity.CorrectiveAction.CorrectiveActionStatus
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class CorrectiveActionResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this("/api/enrolment", accessToken)

    fun getList(
        enrolmentId: String,
        status: CorrectiveActionStatus? = null,
    ): Response {
        var queryString = ""
        if (status != null) {
            queryString += "?status=$status"
        }
        return get("$enrolmentId/corrective-action$queryString")
    }

    private fun get(uriPath: String): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri/$uriPath")
        return spec.andReturn()
    }
}
