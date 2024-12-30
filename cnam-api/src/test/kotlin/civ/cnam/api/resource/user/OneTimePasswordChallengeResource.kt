package civ.cnam.api.resource.user

import dev.dry.core.data.model.type.DeviceAttributes
import dev.dry.core.data.model.type.NewDeviceAttributesData
import dev.dry.core.data.model.type.NewLocationCoordinatesData
import dev.dry.restassured.test.resource.AbstractResource
import dev.dry.user.api.user.auth.OneTimePasswordCreateOperation
import dev.dry.user.api.user.auth.OneTimePasswordVerifyOperation.OneTimePasswordVerifyRequest
import io.restassured.RestAssured
import io.restassured.response.Response

class OneTimePasswordChallengeResource(private val resourceUri: String) : AbstractResource() {
    constructor(): this(resourceUri(OneTimePasswordCreateOperation::class))

    fun verify(otpChallengeId: String, otp: String): Response = verify(
        OneTimePasswordVerifyRequest(otpChallengeId = otpChallengeId, otp = otp, deviceAttributes = deviceAttributes())
    )

    fun verify(requestBody: OneTimePasswordVerifyRequest): Response {
        return RestAssured.given()
            .spec(baseRequestSpec)
            .body(requestBody)
            .post("$resourceUri/verify")
            .andReturn()
    }

    private fun deviceAttributes(): NewDeviceAttributesData = NewDeviceAttributesData(
        deviceIdentifier = "111111111",
        model = "Samsung Galaxy",
        operatingSystem = DeviceAttributes.OperatingSystem.ANDROID,
        networkConnectionMode = DeviceAttributes.NetworkConnectionMode.WIFI,
        location = NewLocationCoordinatesData(
            latitude = 5.345317,
            longitude = -4.024429,
        )
    )
}
