package civ.cnam.api.resource.user

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import dev.dry.user.api.user.permission.PermissionGetListOperation
import io.restassured.RestAssured
import io.restassured.response.Response

class PermissionResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(PermissionGetListOperation::class), accessToken)

    fun listByKeyword(keyword: String?): Response {
        val spec = RestAssured.given()
            .spec(baseRequestSpec)
        if (keyword != null) {
            spec.queryParam("keyword", keyword)
        }
        return spec.get(resourceUri).andReturn()
    }
}
