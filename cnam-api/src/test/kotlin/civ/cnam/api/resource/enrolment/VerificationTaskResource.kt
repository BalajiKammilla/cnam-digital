package civ.cnam.api.resource.enrolment

import civ.cnam.enrolment.domain.model.entity.VerificationOutboxTask.VerificationType
import civ.cnam.enrolment.domain.model.type.enrolment.VerificationTaskFilter.DateRangeProperty
import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import io.restassured.RestAssured
import io.restassured.response.Response

class VerificationTaskResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this("/api/management/enrolment/verification-task", accessToken)

    fun getList(
        pageNumber: Int = 1,
        pageSize: Int = 10,
        dateRangeProperty: DateRangeProperty? = null,
        fromDate: String? = null,
        toDate: String? = null,
        type: VerificationType? = null,
        enrolmentId: String? = null,
    ): Response {
        var queryString = "?pageNumber=$pageNumber&pageSize=$pageSize"
        if (dateRangeProperty != null) {
            queryString += "&dateRangeProperty=$dateRangeProperty"
        }
        if (fromDate != null) {
            queryString += "&fromDate=$fromDate"
        }
        if (toDate != null) {
            queryString += "&toDate=$toDate"
        }
        if (type != null) {
            queryString += "&type=$type"
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
