package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.mutation.enrolment.UpdateDedupeAttributes
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeMatchAttributes
import dev.dry.common.function.Either
import dev.dry.common.function.Either.Companion.left
import dev.dry.common.function.Either.Companion.right
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class UpdateDedupeAttributesJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
) : UpdateDedupeAttributes {
    override operator fun invoke(
        id: PartialEnrolment.ID,
        dedupeAttributes: DedupeMatchAttributes,
    ): Either<EnrolmentNotFound, Unit> {
        val enrolment = findPartialEnrolment(id)
            .fold({ return left(it) }, { it })

        val enrolmentDetails = enrolment.enrolmentDetails
            ?: return left(EnrolmentNotFound)

        val identityDocument = enrolment.identityDocument
            ?: return left(EnrolmentNotFound)

        enrolmentDetails.personalDetails.firstNames = dedupeAttributes.firstName
        enrolmentDetails.personalDetails.lastName = dedupeAttributes.lastName

        enrolmentDetails.birthDetails.date = dedupeAttributes.dateOfBirth

        identityDocument.documentTypeCode = dedupeAttributes.identityDocumentTypeCode
        identityDocument.documentNumber = dedupeAttributes.identityDocumentNumber

        jpaOperations.persist(enrolment)

        return right(Unit)
    }
}
