package civ.cnam.api.resource

import dev.dry.restassured.test.resource.AbstractAuthenticatedResource
import dev.dry.restassured.test.validation.ThenCompleted
import dev.dry.user.api.user.permission.PermissionGetListOperation
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.ValidatableResponse

class PermissionResource(
    private val resourceUri: String,
    accessToken: String,
) : AbstractAuthenticatedResource(accessToken) {
    constructor(accessToken: String): this(resourceUri(PermissionGetListOperation::class), accessToken)

    fun listByKeyword(keyword: String?, then: ValidatableResponse.() -> Unit = ThenCompleted()) {
        Given {
            spec(baseRequestSpec)
        } When {
            get(resourceUri)
        } Then {
            log().body(true)
            then(this)
        }
    }
}
