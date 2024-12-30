package civ.cnam.api.data

import dev.dry.core.data.model.value.MobileNumber
import net.datafaker.Faker
import java.util.*

open class BaseBuilder {
    protected val faker: Faker get() = fakerInstance

    /* +225 01 xx xx xx xx / +225 05 xx xx xx xx / +225 07 xx xx xx xx / +225 25 xx xx xx xx */
    protected fun mobileNumber(): MobileNumber = MobileNumber(
        "07" + EnrolmentDetailsBuilder.faker.phoneNumber().subscriberNumber(8)
    )

    companion object {
        private var fakerInstance = Faker(Locale("fr", "CIV"))
    }
}