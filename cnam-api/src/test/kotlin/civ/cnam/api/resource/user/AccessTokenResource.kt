package civ.cnam.api.resource.user

import dev.dry.core.data.model.type.DeviceAttributes.NetworkConnectionMode
import dev.dry.core.data.model.type.DeviceAttributes.OperatingSystem
import dev.dry.core.data.model.type.NewDeviceAttributesData
import dev.dry.core.data.model.type.NewLocationCoordinatesData
import dev.dry.restassured.test.resource.AccessTokenResource
import dev.dry.user.api.user.auth.AccessTokenCreateOperation
import dev.dry.user.api.user.auth.AccessTokenCreateOperation.AccessTokenCreateRequest
import io.restassured.response.Response

class AccessTokenResource : AccessTokenResource<AccessTokenCreateRequest>(
    resourceOperationClass = AccessTokenCreateOperation::class,
) {
    fun create(
        userName: String,
        password: String,
    ): Response {
        return create(
            AccessTokenCreateRequest(
                userName = userName,
                password = password,
                deviceAttributes = NewDeviceAttributesData(
                    deviceIdentifier = "111111111",
                    model = "Samsung Galaxy",
                    operatingSystem = OperatingSystem.ANDROID,
                    networkConnectionMode = NetworkConnectionMode.WIFI,
                    location = NewLocationCoordinatesData(
                        latitude = 5.345317,
                        longitude = -4.024429,
                    )
                ),
            ),
        )
    }
}
