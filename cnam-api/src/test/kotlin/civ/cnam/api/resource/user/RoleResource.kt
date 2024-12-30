package civ.cnam.api.resource.user

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import dev.dry.user.api.user.role.RoleCreateOperation
import dev.dry.user.api.user.role.RoleCreateOperation.RoleCreateRequest
import dev.dry.user.api.user.role.RoleUpdateOperation.RoleUpdateRequest
import io.restassured.RestAssured
import io.restassured.response.Response

class RoleResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(RoleCreateOperation::class), accessToken)

    fun create(
        title: String,
        description: String,
        vararg permissionValues: String,
    ): Response {
        val request = RoleCreateRequest(
            title = title,
            description = description,
            permissionValues = permissionValues.toSet(),
        )
        return create(request)
    }

    fun create(request: RoleCreateRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .post(resourceUri)
            .andReturn()
    }

    fun update(
        roleName: String,
        description: String?,
        vararg permissionValues: String?,
    ): Response {
        val request = RoleUpdateRequest(
            description = description,
            permissionValues = permissionValues.mapNotNull { it }.toSet(),
        )
        return update(roleName, request)
    }

    fun update(
        roleName: String,
        request: RoleUpdateRequest,
    ): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .patch("$resourceUri/$roleName")
            .andReturn()
    }

    fun getList(): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .get(resourceUri)
            .andReturn()
    }
}
