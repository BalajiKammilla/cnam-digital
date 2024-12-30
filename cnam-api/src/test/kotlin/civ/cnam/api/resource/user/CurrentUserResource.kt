package civ.cnam.api.resource.user

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import dev.dry.user.api.me.CurrentUserUpdateOperation.UpdateCurrentUserRequest
import io.restassured.RestAssured
import io.restassured.response.Response

class CurrentUserResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(CurrentUserResource::class), accessToken)

    fun update(displayName: String? = null, password: String? = null): Response {
        val request = UpdateCurrentUserRequest(
            displayName = displayName,
            password = password,
        )
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .patch(resourceUri)
            .andReturn()
    }

    fun update(request: UpdateCurrentUserRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .post(resourceUri)
            .andReturn()
    }
}
