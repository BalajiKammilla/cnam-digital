package civ.cnam.api.resource.alert

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class AlertResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this("/api/alert", accessToken)

    fun get(pageNumber: Int = 1, pageSize: Int = 10, channel: String? = null, recipient: String? = null): Response {
        var queryString = "?pageNumber=$pageNumber&pageSize=$pageSize&recipient=$recipient"
        if (channel != null) {
            queryString += "&channel=$channel"
        }
        if (recipient != null) {
            queryString += "&recipient=$recipient"
        }
        return get(queryString)
    }

    private fun get(uriPath: String): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri/$uriPath")
        return spec.andReturn()
    }
}
