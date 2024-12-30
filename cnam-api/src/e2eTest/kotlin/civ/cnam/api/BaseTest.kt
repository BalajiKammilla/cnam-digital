package civ.cnam.api

import civ.cnam.api.resource.AccessTokenResource
import civ.cnam.api.resource.PermissionResource
import dev.dry.user.api.user.permission.PermissionGetListOperation
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.common.http.TestHTTPResource

@QuarkusTestResource(PostgresTestResource::class)
abstract class BaseTest {
    protected fun accessToken(): AccessTokenResource = AccessTokenResource()

    @TestHTTPEndpoint(PermissionGetListOperation::class)
    @TestHTTPResource
    private var permissionResourceUri: String? = null
    protected fun permission(accessToken: String): PermissionResource =
        PermissionResource(resourceUri(permissionResourceUri), accessToken)

    companion object {
        private fun resourceUri(resourceUri: String?): String {
            if (resourceUri == null) {
                throw IllegalStateException("Resource URI is null")
            }
            return resourceUri
        }

        const val CNAM_USER_EMAIL = "test@aptiway.com"
        const val CNAM_USER_PASSWORD = "Password#01"
    }
}
