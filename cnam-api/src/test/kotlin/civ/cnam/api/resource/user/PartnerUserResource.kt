package civ.cnam.api.resource.user

import civ.cnam.enrolment.user.api.PartnerUserCreateOperation
import civ.cnam.enrolment.user.api.PartnerUserCreateOperation.PartnerUserCreateRequest
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class PartnerUserResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(PartnerUserCreateOperation::class), accessToken)

    fun create(
        mobileNumber: String,
        displayName: String,
        password: String,
        organisationNumber: String,
    ): Response {
        val request = PartnerUserCreateRequest(
            mobileNumber = mobileNumber,
            displayName = displayName,
            password = password,
            organisationNumber = organisationNumber
        )
        return create(request)
    }

    fun create(request: PartnerUserCreateRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(request)
            .post(resourceUri)
            .andReturn()
    }

    fun getList(pageNumber: Int, pageSize: Int): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .get("$resourceUri?pageNumber=$pageNumber&pageSize=$pageSize")
            .andReturn()
    }
}
