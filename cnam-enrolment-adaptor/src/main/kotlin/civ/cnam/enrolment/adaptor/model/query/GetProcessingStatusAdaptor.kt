package civ.cnam.enrolment.adaptor.model.query

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.model.query.enrolment.GetProcessingStatus
import civ.cnam.enrolment.domain.model.query.enrolment.GetProcessingStatus.GetProcessingStatusError
import civ.cnam.enrolment.domain.model.query.enrolment.GetProcessingStatus.ProcessingStatus
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.core.http.client.ApiClient
import dev.dry.core.serialization.ObjectReader
import dev.dry.core.serialization.ObjectWriter
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class GetProcessingStatusAdaptor(
    objectReader: ObjectReader,
    objectWriter: ObjectWriter,
) : GetProcessingStatus {
    private val apiClient = ApiClient(
        name = "Enrolment Status",
        baseUri = "https://macartecmu.ci/FR/",
        commonRequestDecorator = null,
        commonInterceptor = null,
        objectReader,
        objectWriter,
        allowInsecureTls = false,
        enableRequestAndResponseLogging = false
    )

    override fun invoke(externalId: EnrolmentId): Either<GetProcessingStatusError, ProcessingStatus> {
        return Either.right(ProcessingStatus(null, null, null))
    }
}