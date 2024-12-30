package civ.cnam.api.user

import civ.cnam.api.BaseTest
import civ.cnam.api.UserApiTestProfile
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.*

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestProfile(UserApiTestProfile::class)
class PermissionGetListTest : BaseTest() {
    @Test
    fun getPermissionList() {
        val accessToken = accessToken().create(
            userName = CNAM_USER_EMAIL,
            password = CNAM_USER_PASSWORD,
        )
        permission(accessToken).listByKeyword(null)
    }
}
