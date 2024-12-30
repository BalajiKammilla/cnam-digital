package civ.cnam.api.data

import civ.cnam.enrolment.user.api.PublicUserCreateOperation.PublicUserCreateRequest

object UserBuilder : BaseBuilder()  {
    fun publicUserCreateRequest(): PublicUserCreateRequest {
        val mobileNumber = mobileNumber()
        val firstName = faker.name().firstName()
        val lastName = faker.name().lastName()
        return PublicUserCreateRequest(
            mobileNumber = mobileNumber.value,
            displayName = "$firstName $lastName",
            password = mobileNumber.value.takeLast(5),
        )
    }
}
