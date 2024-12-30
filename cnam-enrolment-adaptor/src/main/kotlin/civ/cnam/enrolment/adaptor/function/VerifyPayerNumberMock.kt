package civ.cnam.enrolment.adaptor.function

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.CNAM_PAYER_VERIFICATION_ADAPTOR
import civ.cnam.enrolment.domain.function.VerifyPayerNumber
import civ.cnam.enrolment.domain.model.value.CNAMNumber
import io.quarkus.arc.lookup.LookupIfProperty
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
@LookupIfProperty(name = CNAM_PAYER_VERIFICATION_ADAPTOR, stringValue = "mock")
class VerifyPayerNumberMock : VerifyPayerNumber {
    override fun invoke(number: CNAMNumber): Boolean {
        return number.value.equals("3843457837159")
    }
}
