package civ.cnam.api.resource.enrolment

import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response
import java.time.LocalDate

class EnrolmentActionResource(
    private val resourceUri: String,
    accessToken: String) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this("/api/management/enrolment/action", accessToken)

    fun getList(
        pageNumber: Int = 1,
        pageSize: Int = 10,
        fromCreatedAt: LocalDate? = null,
        toCreatedAt: LocalDate? = null,
        kind: EnrolmentActionKind? = null,
        mobileNumber: MobileNumber? = null,
        enrolmentId: String? = null
    ): Response{
        var queryString = "?pageNumber=$pageNumber&pageSize=$pageSize"
        if (fromCreatedAt != null) {
            queryString += "&fromCreatedAt=$fromCreatedAt"
        }
        if (toCreatedAt != null) {
            queryString += "&toCreatedAt=$toCreatedAt"
        }
        if (kind != null) {
            queryString += "&kind=$kind"
        }
        if (mobileNumber != null) {
            queryString += "&mobileNumber=$mobileNumber"
        }
        if (enrolmentId != null) {
            queryString += "&enrolmentId=$enrolmentId"
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