package civ.cnam.enrolment.adaptor.model.mutation.enrolment

import civ.cnam.enrolment.adaptor.model.entity.DedupeMatchEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.model.entity.DedupeMatch
import civ.cnam.enrolment.domain.model.entity.ReviewTask.ReviewTaskType
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateOrUpdateDedupeMatch
import civ.cnam.enrolment.domain.model.mutation.enrolment.CreateReviewTask
import civ.cnam.enrolment.domain.model.type.enrolment.DedupeMatchAttributes
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.ENROLMENT_DEDUPE_MATCHED
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.map
import dev.dry.core.jpa.operations.JpaOperations
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.transaction.Transactional

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class CreateOrUpdateDedupeMatchJpa(
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
    private val createEnrolmentAction: CreateEnrolmentAction,
    private val createReviewTask: CreateReviewTask,
) : CreateOrUpdateDedupeMatch {
    @Transactional
    override operator fun invoke(completedEnrolment: CompletedEnrolment): Either<CodedError, DedupeMatch?> {
        val enrolmentId = completedEnrolment.enrolmentId
        val personalDetails = completedEnrolment.enrolmentDetails.personalDetails
        val identityDocument = completedEnrolment.identityDocument
        val dedupeMatchAttributes = DedupeMatchAttributes(
            firstName = personalDetails.firstNames,
            lastName = personalDetails.lastName,
            dateOfBirth = completedEnrolment.enrolmentDetails.birthDetails.date,
            identityDocumentTypeCode = identityDocument.documentTypeCode,
            identityDocumentNumber = identityDocument.documentNumber,
        )

        val dedupeMatch = createOrUpdateDedupeMatch(enrolmentId, dedupeMatchAttributes)
            .fold({ return Either.left(it) }, { it })

        if (dedupeMatch != null) {
            createReviewTask(completedEnrolment.enrolmentId, ReviewTaskType.DEDUPE)
                .fold({ return Either.left(it) }, {})
        }

        return Either.right(dedupeMatch)
    }

    private fun createOrUpdateDedupeMatch(
        enrolmentId: EnrolmentId,
        dedupeMatchAttributes: DedupeMatchAttributes
    ): Either<EnrolmentErrors.EnrolmentNotFound, DedupeMatchEntity?> {
        val existingDedupeMatch = findDedupeMatchWithAttributesOrNull(dedupeMatchAttributes)
        return if (existingDedupeMatch == null) {
            val matchingEnrolments = findEnrolmentsWithMatchingAttributes(dedupeMatchAttributes)
            return if (matchingEnrolments.size > 1) {
                val newDedupeMatch = with(mapToDedupeMatch(dedupeMatchAttributes)) {
                    matchingEnrolments.forEach { enrolment ->
                        enrolment.dedupeMatch = this
                    }
                    enrolments.addAll(matchingEnrolments)
                    jpaOperations.persist(this).also {
                        matchingEnrolments.forEach { enrolment ->
                            createEnrolmentAction(ENROLMENT_DEDUPE_MATCHED, enrolment)
                        }
                    }
                }
                Either.right(newDedupeMatch)
            } else {
                Either.right(null)
            }
        } else {
            findPartialEnrolment(enrolmentId).map { enrolment ->
                enrolment.dedupeMatch = existingDedupeMatch
                existingDedupeMatch.enrolments.add(enrolment)
                jpaOperations.persist(existingDedupeMatch).also {
                    createEnrolmentAction(ENROLMENT_DEDUPE_MATCHED, enrolment)
                }
            }
        }
    }

    private fun findDedupeMatchWithAttributesOrNull(attributes: DedupeMatchAttributes): DedupeMatchEntity? {
        val jpql = """
            SELECT dm FROM DedupeMatch dm 
            WHERE dm.firstName = :firstName
            AND dm.lastName = :lastName
            AND dm.dateOfBirth = :dateOfBirth
            AND dm.identityDocumentNumber = :identityDocumentNumber
            AND dm.identityDocumentTypeCode = :identityDocumentTypeCode
        """.trimIndent()
        return jpaOperations.querySingleResultOrNull(DedupeMatchEntity::class, jpql) {
            parameter("firstName", attributes.firstName.value)
            parameter("lastName", attributes.lastName.value)
            parameter("dateOfBirth", attributes.dateOfBirth)
            parameter("identityDocumentNumber", attributes.identityDocumentNumber.value)
            parameter("identityDocumentTypeCode", attributes.identityDocumentTypeCode.value)
        }
    }

    private fun findEnrolmentsWithMatchingAttributes(
        attributes: DedupeMatchAttributes
    ): List<PartialEnrolmentEntity> {
        val jpql = """
            SELECT pe FROM PartialEnrolment pe 
            JOIN pe.identityDocument id 
            JOIN pe.enrolmentDetails ed
            WHERE id.documentNumber = :identityDocumentNumber 
            AND id.documentTypeCode = :identityDocumentTypeCode
            AND ed.personalDetails.firstNames = :firstName
            AND ed.personalDetails.lastName = :lastName
            AND ed.birthDetails.date = :dateOfBirth
        """.trimIndent()
        return jpaOperations.queryList(PartialEnrolmentEntity::class, jpql) {
            parameter("firstName", attributes.firstName.value)
            parameter("lastName", attributes.lastName.value)
            parameter("dateOfBirth", attributes.dateOfBirth)
            parameter("identityDocumentNumber", attributes.identityDocumentNumber.value)
            parameter("identityDocumentTypeCode", attributes.identityDocumentTypeCode.value)
        }
    }

    protected fun mapToDedupeMatch(attributes: DedupeMatchAttributes): DedupeMatchEntity {
        return DedupeMatchEntity(
            firstName = attributes.firstName,
            lastName = attributes.lastName,
            dateOfBirth = attributes.dateOfBirth,
            identityDocumentTypeCode = attributes.identityDocumentTypeCode,
            identityDocumentNumber = attributes.identityDocumentNumber,
        )
    }
}