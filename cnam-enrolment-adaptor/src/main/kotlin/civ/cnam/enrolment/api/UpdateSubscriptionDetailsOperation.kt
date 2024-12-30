package civ.cnam.enrolment.api

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_API_ENABLED
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.domain.command.UpdateSubscriptionDetails
import civ.cnam.enrolment.domain.command.UpdateSubscriptionDetails.PayerProofOfIdentity
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateSubscription.UpdateSubscriptionError
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.type.attachment.DocumentPage
import civ.cnam.enrolment.domain.model.type.enrolment.partial.SubscriptionDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.function.Either
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jaxrs.response.DefaultApiCompletedNoContentResponse
import dev.dry.core.jaxrs.response.DefaultApiErrorResponse
import dev.dry.core.security.auth.model.value.RoleName
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

@Singleton
@Path("/enrolment/{enrolmentId}/details/subscription")
@Tags(Tag(name = "Enrolment"))
@IfBuildProperty.List(value = [
    IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true"),
    IfBuildProperty(name = CNAM_ENROLMENT_API_ENABLED, stringValue = "true")
])
class UpdateSubscriptionDetailsOperation(private val updateSubscriptionDetails: UpdateSubscriptionDetails) {
    @PUT
    @RolesAllowed(RoleName.AUTHENTICATED_USER)
    @Operation(
        summary = "Update subscription details for an enrolment",
        description = "Subscription details may be updated when there is a pending corrective action"
    )
    @APIResponse(responseCode = "200", content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = UpdateSubscriptionDetailsResponse::class)
        )
    ])
    @APIResponse(content = [
        Content(
            mediaType = APPLICATION_JSON,
            schema = Schema(implementation = UpdateSubscriptionDetailsErrorResponse::class)
        )
    ])
    @Produces(APPLICATION_JSON)
    operator fun invoke(
        @Context ctx: SecurityContext,
        @PathParam("enrolmentId") enrolmentId: String,
        @Valid @RequestBody request: UpdateSubscriptionDetailsRequest,
    ): Either<UpdateSubscriptionError, Unit> {
        return updateSubscriptionDetails(
            EnrolmentId(enrolmentId),
            MobileNumber(ctx.userPrincipal.name),
            request.subscriptionDetails,
            request.payerProofOfIdentity?.toPayerProofOfIdentity(),
        )
        //return Either.right(Unit)
    }

    /*
    class PayerData(
        val cnamNumber: String,
        val enrolmentId: String,
        val firstName: String,
        val lastName: String,
    ) {
        fun toNaturalPersonPayer() = NaturalPersonPayerData(
            cnamNumber = CNAMNumber(cnamNumber),
            enrolmentId = EnrolmentId(enrolmentId),
            firstName = FirstName(firstName),
            lastName = LastName(lastName),
        )
    }

    class PaymentDetailsData(
        val paidByCode: String,
        val payer: PayerData?,
    ) {
        fun toSubscriptionDetails(): SubscriptionDetailsData = SubscriptionDetailsData(
            PayerTypeCode(paidByCode),
            payer?.toNaturalPersonPayer()
        )
    }*/

    class PayerProofOfIdentityData(
        val documentTypeCode: String,
        val pages: List<DocumentPage>,
    ) {
        fun toPayerProofOfIdentity(): PayerProofOfIdentity = PayerProofOfIdentity(
            DocumentTypeCode(documentTypeCode),
            pages
        )
    }

    class UpdateSubscriptionDetailsRequest(
        val subscriptionDetails: SubscriptionDetailsData?,
        val payerProofOfIdentity: PayerProofOfIdentityData?,
    )

    class UpdateSubscriptionDetailsResponse : DefaultApiCompletedNoContentResponse()
    class UpdateSubscriptionDetailsErrorResponse(
        error: UpdateSubscriptionError
    ) : DefaultApiErrorResponse<UpdateSubscriptionError>(error)
}
