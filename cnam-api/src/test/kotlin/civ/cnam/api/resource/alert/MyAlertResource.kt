package civ.cnam.api.resource.alert

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class MyAlertResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this("/api/me/alert", accessToken)

    fun get(pageNumber: Int = 1, pageSize: Int = 10): Response {
        return get("?pageNumber=$pageNumber&pageSize=$pageSize")
    }

    private fun get(uriPath: String): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri/$uriPath")
        return spec.andReturn()
    }
}
