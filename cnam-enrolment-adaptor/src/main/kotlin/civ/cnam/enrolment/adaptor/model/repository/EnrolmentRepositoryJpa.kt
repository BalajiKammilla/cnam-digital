package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.adaptor.model.entity.EnrolmentDetailsEntity
import civ.cnam.enrolment.adaptor.model.entity.EnrolmentUserEntity
import civ.cnam.enrolment.adaptor.model.entity.IdentityDocumentEntity
import civ.cnam.enrolment.adaptor.model.entity.PartialEnrolmentEntity
import civ.cnam.enrolment.adaptor.model.entity.SupportingDocumentEntity
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.CreateEnrolmentAction
import civ.cnam.enrolment.adaptor.model.mutation.enrolment.FindPartialEnrolmentJpa
import civ.cnam.enrolment.adaptor.model.query.GetEnrolmentUserJpa
import civ.cnam.enrolment.constants.EnrolmentBuildProperty.CNAM_ENROLMENT_ENABLED
import civ.cnam.enrolment.constants.EnrolmentConfigurationProperty.DEFAULT_JPA_OPERATIONS
import civ.cnam.enrolment.domain.error.EnrolmentErrors
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFound
import civ.cnam.enrolment.domain.error.EnrolmentErrors.EnrolmentNotFoundError
import civ.cnam.enrolment.domain.function.MapAgeRangeToDateRange
import civ.cnam.enrolment.domain.function.MapPartialToCompletedEnrolment
import civ.cnam.enrolment.domain.function.MapPartialToEnrolmentListItem
import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment.ApprovalStatus
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.referencedata.GenderCode
import civ.cnam.enrolment.domain.model.referencedata.TitleCode
import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentFilter
import civ.cnam.enrolment.domain.model.type.enrolment.EnrolmentListItem
import civ.cnam.enrolment.domain.model.type.enrolment.completed.CompletedEnrolment
import civ.cnam.enrolment.domain.model.type.enrolment.partial.AddressData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.BirthDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ContactDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.EnrolmentDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.PersonalDetailsData
import civ.cnam.enrolment.domain.model.type.enrolment.partial.ProfessionalDetailsData
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.ENROLMENT_APPROVED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.ENROLMENT_COMPLETED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.ENROLMENT_DETAILS_UPDATED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.ENROLMENT_PROCESSED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.FINGERPRINTS_ATTACHED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.IDENTITY_DOCUMENT_ATTACHED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.PHOTO_ATTACHED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.SIGNATURE_ATTACHED
import civ.cnam.enrolment.domain.model.value.EnrolmentActionKind.SUPPORTING_DOCUMENT_ATTACHED
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import dev.dry.common.error.CodedError
import dev.dry.common.function.Either
import dev.dry.common.function.flatten
import dev.dry.common.function.map
import dev.dry.common.function.mapLeft
import dev.dry.common.model.value.IsoCountryCode
import dev.dry.common.time.TimeProvider
import dev.dry.core.data.model.value.EmailAddress
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.data.model.value.toUserName
import dev.dry.core.data.pagination.Page
import dev.dry.core.jpa.operations.JpaOperations
import dev.dry.user.domain.error.UserErrors
import io.quarkus.arc.properties.IfBuildProperty
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Singleton
@IfBuildProperty(name = CNAM_ENROLMENT_ENABLED, stringValue = "true")
class EnrolmentRepositoryJpa(
    private val mapPartialToCompletedEnrolment: MapPartialToCompletedEnrolment,
    private val mapAgeRangeToDateRange: MapAgeRangeToDateRange,
    private val timeProvider: TimeProvider,
    @Named(DEFAULT_JPA_OPERATIONS)
    private val jpaOperations: JpaOperations,
    private val findPartialEnrolment: FindPartialEnrolmentJpa,
    private val mapPartialToEnrolmentListItem: MapPartialToEnrolmentListItem,
    private val createEnrolmentAction: CreateEnrolmentAction,
    private val getEnrolmentUser: GetEnrolmentUserJpa,
) : EnrolmentRepository {
    override fun findPartialEnrolmentOrNull(enrolmentId: EnrolmentId): PartialEnrolmentEntity? {
        return findPartialEnrolment.orNull(enrolmentId)
    }

    override fun findPartialEnrolment(enrolmentId: EnrolmentId): Either<EnrolmentNotFound, PartialEnrolmentEntity> {
        return findPartialEnrolment.invoke(enrolmentId)
    }

    override fun findPartialEnrolment(enrolmentId: PartialEnrolment.ID): Either<EnrolmentNotFound, PartialEnrolment> {
        return findPartialEnrolment.invoke(enrolmentId)
    }

    override fun findPartialEnrolment(
        enrolmentId: EnrolmentId,
        mobileNumber: MobileNumber,
    ): Either<EnrolmentNotFoundError, PartialEnrolmentEntity> {
        return findPartialEnrolment.invoke(enrolmentId, mobileNumber)
    }

    @Transactional
    override fun createPartialEnrolment(
        mobileNumber: MobileNumber,
        fingerprintsAttachmentId: FingerprintsAttachmentId?,
        photoAttachmentId: PhotoAttachmentId?,
        signatureAttachmentId: SignatureAttachmentId?,
        enrolmentDetails: EnrolmentDetailsData?
    ): PartialEnrolmentEntity {
        val enrolment = PartialEnrolmentEntity(
            id = PartialEnrolment.ID.NULL,
            mobileNumber = mobileNumber.toInternationalFormat(IsoCountryCode.CI),
            enrolmentId = EnrolmentId.generate(),
            startedAt = timeProvider.now(),
            completedAt = null,
            verifiedAt = null,
            processedAt = null,
            reviewTaskPendingCount = 0,
            verificationTaskPendingCount = 0,
            correctiveActionPendingCount = 0,
            approvalStatus = null,
            identityDocument = null,
            identityDocumentOcrSucceeded = null,
            photoAttachmentId = null,
            fingerprintsAttachmentId = null,
            signatureAttachmentId = null,
            enrolmentDetails = null,
            dedupeMatch = null,
            supportingDocuments = mutableListOf(),
            reviewTasks = mutableListOf(),
            verificationChecks = mutableListOf(),
            correctiveActions = mutableListOf(),
        )

        jpaOperations.persist(enrolment)

        updateAttachmentIds(enrolment, photoAttachmentId, fingerprintsAttachmentId, signatureAttachmentId)

        createOrUpdateEnrolmentDetails(enrolment, enrolmentDetails)

        return enrolment
    }

    @Transactional
    override fun updatePartialEnrolment(
        mobileNumber: MobileNumber,
        enrolmentId: EnrolmentId,
        fingerprintsAttachmentId: FingerprintsAttachmentId?,
        photoAttachmentId: PhotoAttachmentId?,
        signatureAttachmentId: SignatureAttachmentId?,
        enrolmentDetails: EnrolmentDetailsData?
    ): Either<EnrolmentNotFound, PartialEnrolment> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            createOrUpdateEnrolmentDetails(enrolment, enrolmentDetails)
            updateAttachmentIds(enrolment, photoAttachmentId, fingerprintsAttachmentId, signatureAttachmentId)
            jpaOperations.persist(enrolment)
            enrolment
        }
    }

    private fun updateAttachmentIds(
        enrolment: PartialEnrolmentEntity,
        photoAttachmentId: PhotoAttachmentId?,
        fingerprintsAttachmentId: FingerprintsAttachmentId?,
        signatureAttachmentId: SignatureAttachmentId?
    ) {
        photoAttachmentId?.also {
            enrolment.photoAttachmentId = it
            createEnrolmentAction(PHOTO_ATTACHED, enrolment)
        }
        fingerprintsAttachmentId?.also {
            enrolment.fingerprintsAttachmentId = it
            createEnrolmentAction(FINGERPRINTS_ATTACHED, enrolment)
        }
        signatureAttachmentId?.also {
            enrolment.signatureAttachmentId = it
            createEnrolmentAction(SIGNATURE_ATTACHED, enrolment)
        }
    }

    override fun findEnrolmentsPendingProcessing(pageNumber: Int, pageSize: Int): Page<EnrolmentId> {
        // TODO("update query")
        val jpql = """
            SELECT pe FROM PartialEnrolment pe 
            WHERE pe.verifiedAt IS NOT NULL 
            AND pe.processedAt IS NULL
            AND pe.approvalStatus = ApprovalStatus.APPROVED
        """.trimIndent()
        val jpqlCount = """
            SELECT COUNT(pe) FROM PartialEnrolment pe 
            WHERE pe.verifiedAt IS NOT NULL 
            AND pe.processedAt IS NULL
            AND pe.approvalStatus = ApprovalStatus.APPROVED
        """.trimIndent()
        return jpaOperations.queryPage(
            PartialEnrolment::class,
            jpql = jpql,
            jpqlCount = jpqlCount,
            pageNumber = pageNumber,
            pageSize = pageSize,
        ).mapContent { it.enrolmentId }
    }

    override fun findCompletedEnrolments(mobileNumber: MobileNumber): List<EnrolmentListItem> {
        val jpql = "SELECT pe FROM PartialEnrolment pe WHERE pe.mobileNumber = :mobileNumber AND pe.completedAt IS NOT NULL"
        return jpaOperations.queryList(PartialEnrolmentEntity::class, jpql) {
            parameter("mobileNumber", mobileNumber.value)
        }.map(mapPartialToEnrolmentListItem::invoke)
    }

    override fun findByFilter(filter: EnrolmentFilter, pageNumber: Int, pageSize: Int): Page<PartialEnrolmentEntity> {
        return jpaOperations.queryPage(PartialEnrolmentEntity::class, pageNumber = pageNumber, pageSize = pageSize) {
            val mobileNumber = attribute<String>("mobileNumber")
            val completedAt = attribute<LocalDateTime>("completedAt")
            val processedAt = attribute<LocalDateTime>("processedAt")
            val dedupeMatchId = attribute<Long>("dedupeMatchId")
            val identityDocumentOcrSucceeded = attribute<Boolean>("identityDocumentOcrSucceeded")
            val approvalStatus = attribute<ApprovalStatus>("approvalStatus")
            val enrolmentDetails = join<EnrolmentDetailsEntity>("enrolmentDetails")
            val dateOfBirth = enrolmentDetails.get<BirthDetailsData>("birthDetails").get<LocalDate>("date")

            val dateProperty = when(filter.dateRangeProperty ?: EnrolmentFilter.DateRangeProperty.COMPLETED_AT) {
                EnrolmentFilter.DateRangeProperty.STARTED_AT -> attribute("startedAt")
                EnrolmentFilter.DateRangeProperty.COMPLETED_AT -> completedAt
                EnrolmentFilter.DateRangeProperty.APPROVAL_STATUS_UPDATED_AT ->
                    attribute("approvalStatusUpdatedAt")
                EnrolmentFilter.DateRangeProperty.PROCESSING_COMPLETED_AT -> processedAt
                EnrolmentFilter.DateRangeProperty.DATE_OF_BIRTH -> enrolmentDetails
                    .get<BirthDetailsData>("birthDetails").get("date")
            }

            where {
                filter.completed?.also { if (it) isNotNull(completedAt) else isNull(completedAt) }
                filter.approvalRequired?.also {
                    if (it) {
                        or {
                            isNotNull(dedupeMatchId)
                            identityDocumentOcrSucceeded equal false
                        }
                    } else {
                        and {
                            isNull(dedupeMatchId)
                            or {
                                isNull(identityDocumentOcrSucceeded)
                                identityDocumentOcrSucceeded equal true
                            }
                        }
                    }
                }
                filter.approvalStatus?.also { approvalStatus equal it }
                filter.processed?.also { if (it) isNotNull(processedAt) else isNull(processedAt) }
                filter.dateRange?.also { dateProperty between it }
                filter.ageRange?.let(mapAgeRangeToDateRange::invoke)?.also { dateOfBirth between it }
                filter.gender?.also {
                    val titleCode = enrolmentDetails.get<PersonalDetailsData>("personalDetails")
                        .get<String>("titleCode")
                    if (it == GenderCode.MALE) {
                        titleCode equal TitleCode.MISTER.value
                    } else {
                        titleCode notEqual TitleCode.MISTER.value
                    }
                }
                filter.nationality?.also {
                    enrolmentDetails.get<PersonalDetailsData>("personalDetails")
                        .get<String>("currentNationalityCode") equal it.value
                }
                filter.personCategory?.also {
                    enrolmentDetails.get<ProfessionalDetailsData>("professionalDetails")
                        .get<String>("personTypeCode") equal it.value
                }
                filter.location?.also {
                    enrolmentDetails.get<AddressData>("address")
                        .get<String>("subPrefectureCode") equal it.value
                }
                filter.firstNames?.also {
                    enrolmentDetails.get<PersonalDetailsData>("personalDetails")
                        .get<String>("firstNames") like "%$it%"
                }
                filter.lastName?.also {
                    enrolmentDetails.get<PersonalDetailsData>("personalDetails")
                        .get<String>("lastName") like "%$it%"
                }
                filter.mobileNumber?.also {
                    or {
                        mobileNumber like "%${it.value}%"
                        enrolmentDetails.get<ContactDetailsData>("contactDetails")
                            .get<String>("mobileNumber") like "%${it.value}%"
                    }
                }
                filter.emailAddress?.also {
                    enrolmentDetails.get<ContactDetailsData>("contactDetails")
                        .get<String>("email") equal it.value
                }
                filter.enrolmentId?.also {
                    attribute<String>("enrolmentId") equal it.value
                }
            }
        }
    }

    @Transactional
    override fun updateAsProcessed(
        enrolmentId: EnrolmentId,
        status: Boolean,
    ): Either<CodedError, Unit> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            enrolment.processedAt = timeProvider.now()
            jpaOperations.persist(enrolment)
            createEnrolmentAction(ENROLMENT_PROCESSED, enrolment)
        }
    }

    @Transactional
    override fun updateCompletedAt(
        enrolmentId: EnrolmentId,
        completedAt: LocalDateTime
    ): Either<EnrolmentNotFound, Unit> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            enrolment.completedAt = completedAt
            jpaOperations.persist(enrolment)
            createEnrolmentAction(ENROLMENT_COMPLETED, enrolment)
        }
    }

    @Transactional
    override fun createOrUpdateIdentityDocument(
        enrolmentId: EnrolmentId,
        documentNumber: DocumentNumber,
        documentTypeCode: DocumentTypeCode,
        documentAttachmentId: DocumentAttachmentId,
        pageCount: Int,
        ocrSucceeded: Boolean,
    ): Either<EnrolmentNotFound, IdentityDocument> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            createOrUpdateIdentityDocument(
                enrolment,
                documentNumber,
                documentTypeCode,
                documentAttachmentId,
                pageCount,
                ocrSucceeded
            )
        }
    }

    private fun createOrUpdateIdentityDocument(
        enrolment: PartialEnrolmentEntity,
        documentNumber: DocumentNumber,
        documentTypeCode: DocumentTypeCode,
        documentAttachmentId: DocumentAttachmentId,
        pageCount: Int,
        ocrSucceeded: Boolean
    ): IdentityDocumentEntity {
        val existingIdentityDocument = enrolment.identityDocument
        val newOrUpdatedIdentityDocument = if (existingIdentityDocument != null) {
            existingIdentityDocument.documentNumber = documentNumber
            existingIdentityDocument.documentTypeCode = documentTypeCode
            existingIdentityDocument.pageCount = pageCount
            existingIdentityDocument.ocrSucceeded = ocrSucceeded
            enrolment.identityDocumentOcrSucceeded = ocrSucceeded
            existingIdentityDocument
        } else {
            val newIdentityDocument = IdentityDocumentEntity(
                enrolment,
                documentNumber = documentNumber,
                documentTypeCode = documentTypeCode,
                documentAttachmentId = documentAttachmentId,
                pageCount = pageCount,
                ocrSucceeded = ocrSucceeded,
            )
            enrolment.identityDocumentOcrSucceeded = ocrSucceeded
            enrolment.identityDocument = newIdentityDocument
            newIdentityDocument
        }
        jpaOperations.persist(enrolment)
        createEnrolmentAction(IDENTITY_DOCUMENT_ATTACHED, enrolment, newOrUpdatedIdentityDocument.documentAttachmentId.value)
        return newOrUpdatedIdentityDocument
    }

    @Transactional
    override fun createOrReplaceSupportingDocument(
        enrolmentId: EnrolmentId,
        documentAttachmentId: DocumentAttachmentId,
        purpose: SupportingDocument.Purpose,
        documentTypeCode: DocumentTypeCode,
        pageCount: Int
    ): Either<EnrolmentNotFound, SupportingDocument> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            enrolment.supportingDocuments.removeIf {
                it.purpose == purpose && it.documentTypeCode == documentTypeCode
            }
            SupportingDocumentEntity(
                id = SupportingDocument.ID.NULL,
                enrolment = enrolment,
                documentAttachmentId = documentAttachmentId,
                purpose = purpose,
                documentTypeCode = documentTypeCode,
                pageCount = pageCount
            ).also { supportingDocument ->
                enrolment.supportingDocuments.add(supportingDocument)
                createEnrolmentAction(SUPPORTING_DOCUMENT_ATTACHED, enrolment, supportingDocument.documentAttachmentId.value)
                jpaOperations.persist(enrolment)
            }
        }
    }

    override fun findDedupeMatches(enrolmentId: EnrolmentId): Either<CodedError, List<CompletedEnrolment>> {
        return findPartialEnrolment(enrolmentId)
            .mapLeft { EnrolmentErrors.enrolmentNotFound(enrolmentId) }
            .map { enrolment ->
                val dedupeMatch = enrolment.dedupeMatch
                if (dedupeMatch == null) {
                    Either.left(EnrolmentErrors.DEDUPE_NOT_FOUND_FOR_ENROLMENT)
                } else {
                    val enrolments = dedupeMatch.enrolments
                        .filter { it.enrolmentId != enrolmentId }
                        .map(mapPartialToCompletedEnrolment::invoke)
                        .map { either -> either.fold({ null }, { it }) }
                        .mapNotNull { it }
                    Either.right(enrolments)
                }
            }.flatten()
    }

    @Transactional
    override fun updateApprovalStatus(
        enrolmentId: EnrolmentId,
        approvalStatus: ApprovalStatus,
    ): Either<CodedError, Unit> {
        return findPartialEnrolment(enrolmentId).map { enrolment ->
            enrolment.approvalStatus = approvalStatus
            jpaOperations.persist(enrolment)
            createEnrolmentAction(ENROLMENT_APPROVED, enrolment)
        }
    }

    private fun createOrUpdateEnrolmentDetails(
        enrolment: PartialEnrolmentEntity,
        enrolmentDetails: EnrolmentDetailsData?
    ) {
        if (enrolmentDetails != null) {
            val existingEnrolmentDetails = enrolment.enrolmentDetails
            if (existingEnrolmentDetails != null) {
                existingEnrolmentDetails.personalDetails = enrolmentDetails.personalDetails
                existingEnrolmentDetails.birthDetails = enrolmentDetails.birthDetails
                existingEnrolmentDetails.address = enrolmentDetails.address
                existingEnrolmentDetails.contactDetails = ContactDetailsData(enrolmentDetails.contactDetails)
                existingEnrolmentDetails.professionalDetails = enrolmentDetails.professionalDetails
                existingEnrolmentDetails.subscriptionDetails = enrolmentDetails.subscriptionDetails
            } else {
                enrolment.enrolmentDetails = EnrolmentDetailsEntity(
                    enrolment = enrolment,
                    personalDetails = enrolmentDetails.personalDetails,
                    birthDetails = enrolmentDetails.birthDetails,
                    address = enrolmentDetails.address,
                    contactDetails = ContactDetailsData(enrolmentDetails.contactDetails),
                    professionalDetails = enrolmentDetails.professionalDetails,
                    subscriptionDetails = enrolmentDetails.subscriptionDetails
                )
            }
            jpaOperations.persist(enrolment)
            createEnrolmentAction(ENROLMENT_DETAILS_UPDATED, enrolment)
        }
    }

    private fun findBackOfficeUser(
        emailAddress: EmailAddress
    ): Either<CodedError, EnrolmentUserEntity> {
        return getEnrolmentUser.orNull(emailAddress.toUserName())?.let { Either.right(it) }
            ?: Either.left(UserErrors.USER_NOT_FOUND)
    }
}
