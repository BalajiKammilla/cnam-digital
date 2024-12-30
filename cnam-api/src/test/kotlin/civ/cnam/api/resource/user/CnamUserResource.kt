package civ.cnam.api.resource.user

import civ.cnam.enrolment.user.api.CnamUserCreateOperation
import civ.cnam.enrolment.user.api.CnamUserCreateOperation.CnamUserCreateRequest
import civ.cnam.enrolment.user.api.CnamUserUpdateOperation.CnamUserUpdateRequest
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class CnamUserResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(CnamUserCreateOperation::class), accessToken)

    fun create(
        emailAddress: String,
        displayName: String,
        password: String,
        vararg userRoleNames: String,
    ): Response {
        val request = CnamUserCreateRequest(
            emailAddress = emailAddress,
            displayName = displayName,
            password = password,
            userRoles = userRoleNames.toSet(),
        )
        return create(request)
    }

    fun create(request: CnamUserCreateRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .post(resourceUri)
            .andReturn()
    }

    fun update(
        userId: Long,
        displayName: String? = null,
        active: Boolean? = null,
        password: String? = null,
        vararg userRoleNames: String,
    ): Response {
        val request = CnamUserUpdateRequest(
            displayName = displayName,
            active = active,
            password = password,
            userRoleNames = userRoleNames.toSet().ifEmpty { null },
        )
        return update(userId, request)
    }

    fun update(
        userId: Long,
        request: CnamUserUpdateRequest,
    ): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .patch("$resourceUri/$userId")
            .andReturn()
    }

    fun getList(pageNumber: Int, pageSize: Int): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri?pageNumber=$pageNumber&pageSize=$pageSize")
            .andReturn()
    }
}
