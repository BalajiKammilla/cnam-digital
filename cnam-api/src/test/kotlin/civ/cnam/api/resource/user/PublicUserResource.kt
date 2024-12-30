package civ.cnam.api.resource.user

import civ.cnam.enrolment.user.api.PublicUserCreateOperation
import civ.cnam.enrolment.user.api.PublicUserCreateOperation.PublicUserCreateRequest
import dev.dry.restassured.test.resource.AbstractResource
import io.restassured.RestAssured
import io.restassured.response.Response

class PublicUserResource(
    private val resourceUri: String,
) : AbstractResource() {
    constructor(): this(resourceUri(PublicUserCreateOperation::class))

    fun create(
        mobileNumber: String,
        displayName: String,
        password: String,
    ): Response {
        val request = PublicUserCreateRequest(
            mobileNumber = mobileNumber,
            displayName = displayName,
            password = password,
        )
        return create(request)
    }

    fun create(request: PublicUserCreateRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .post(resourceUri)
            .andReturn()
    }
}
