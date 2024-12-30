package civ.cnam.api.context

object UserScenarioContext {
    var userName: String?
        get() = ctx[USER_NAME]
        set(userName) {
            ctx[USER_NAME] = userName
        }

    var password: String?
        get() = ctx[PASSWORD]
        set(password) {
            ctx[PASSWORD] = password
        }

    var accessToken: String?
        get() = ctx[ACCESS_TOKEN]
        set(accessToken) {
            ctx[ACCESS_TOKEN] = accessToken
        }

    var otpChallengeId: String?
        get() = ctx[ONE_TIME_PASSWORD_CHALLENGE_ID]
        set(oneTimePasswordChallengeId) {
            ctx[ONE_TIME_PASSWORD_CHALLENGE_ID] = oneTimePasswordChallengeId
        }

    private val ctx = ScenarioContext
    private const val USER_NAME = "USER_NAME"
    private const val PASSWORD = "PASSWORD"
    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val ONE_TIME_PASSWORD_CHALLENGE_ID = "ONE_TIME_PASSWORD_CHALLENGE_ID"
}