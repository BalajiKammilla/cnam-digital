package civ.cnam.api.data

import civ.cnam.enrolment.constants.RealmNames
import dev.dry.alert.constants.AlertPermissionValue
import dev.dry.user.constants.UserPermissionValue

class TestUser(val userName: String, val password: String, val permissions: Set<String> = emptySet()) {
    class HasPermission(val permission: String, val negate: Boolean = false) {
        fun test(testUser: TestUser): Boolean {
            return testUser.permissions.contains(permission) && !negate
        }

        override fun toString(): String {
            return if (negate) "not($permission)" else permission
        }
    }

    companion object {
        private val allPermissions = setOf(
            UserPermissionValue.PERMISSION_LIST_VALUE,
            UserPermissionValue.ROLE_CREATE_VALUE,
            UserPermissionValue.ROLE_UPDATE_VALUE,
            UserPermissionValue.ROLE_DELETE_VALUE,
            UserPermissionValue.ROLE_LIST_VALUE,

            UserPermissionValue.USER_CREATE_VALUE,
            UserPermissionValue.USER_READ_VALUE,
            UserPermissionValue.USER_UPDATE_VALUE,
            UserPermissionValue.USER_LIST_VALUE,

            UserPermissionValue.PARTNER_CREATE_VALUE,
            UserPermissionValue.PARTNER_READ_VALUE,
            UserPermissionValue.PARTNER_UPDATE_VALUE,
            UserPermissionValue.PARTNER_LIST_VALUE,

            UserPermissionValue.DEVICE_READ_VALUE,

            AlertPermissionValue.ALERT_CREATE,
            AlertPermissionValue.ALERT_LIST,
        )

        private val usersByRealm = mutableMapOf(
            RealmNames.CNAM to listOf(
                TestUser(
                    userName = "test@aptiway.com",
                    password = "Password#01",
                    permissions = allPermissions
                ),
            ),
            RealmNames.PUBLIC to listOf(
                TestUser(userName = "+2250187901407", password = "12345"),
            )
        )

        private val allUsers = usersByRealm.values.flatten()

        const val FIXED_OTP = "1111"

        fun select(
            realmName: String? = null,
            userName: String? = null,
            hasPermissionList: List<HasPermission> = emptyList()
        ): TestUser {
            val users = if (realmName != null) {
                usersByRealm[realmName]
                    ?: throw IllegalArgumentException("test users not found in realm '$realmName'")
            } else allUsers

            if (userName != null) {
                return findWithUserName(userName, users)
                    ?: throw IllegalArgumentException("test user not found with user name '$userName'")
            }

            if (hasPermissionList.isNotEmpty()) {
                return findWithPermissions(users, hasPermissionList).firstOrNull()
                    ?: throw IllegalArgumentException("test user not found with permissions $hasPermissionList")
            }

            return users.first()
        }

        private fun findWithUserName(userName: String, testUsers: List<TestUser>): TestUser? {
            return testUsers.find { testUser -> testUser.userName == userName }
        }

        private fun findWithPermissions(
            testUsers: List<TestUser>,
            hasPermissionList: List<HasPermission>,
        ): List<TestUser> {
            return testUsers.filter { hasPermissions(it, hasPermissionList) }
        }

        private fun hasPermissions(testUser: TestUser, hasPermissionList: List<HasPermission>): Boolean {
            return hasPermissionList.isEmpty() || hasPermissionList.all { it.test(testUser) }
        }
    }

    class TestUserSelector(
        val realmName: String? = null,
        val userName: String? = null,
        val permissions: List<HasPermission> = emptyList()
    ) {
        fun select(usersByRealm: Map<String, List<TestUser>>): TestUser {
            val realmUsers = if (realmName != null) {
                usersByRealm[realmName]
                    ?: throw IllegalArgumentException("test users not found in realm '$realmName'")
            } else usersByRealm.values.flatten()

            if (userName != null) {
                return findWithUserName(userName, realmUsers)
                    ?: throw IllegalArgumentException("test user not found with user name '$userName'")
            }

            if (permissions.isNotEmpty()) {
                return findWithPermissions(realmUsers).firstOrNull()
                    ?: throw IllegalArgumentException("test user not found with permissions $permissions")
            }

            return realmUsers.first()
        }

        private fun findWithUserName(userName: String, testUsers: List<TestUser>): TestUser? {
            return testUsers.find { testUser -> testUser.userName == userName }
        }

        private fun findWithPermissions(testUsers: List<TestUser>): List<TestUser> {
            return testUsers.filter { hasPermissions(it) }
        }

        private fun hasPermissions(testUser: TestUser): Boolean {
            return permissions.isEmpty() || permissions.all { it.test(testUser) }
        }
    }
}
