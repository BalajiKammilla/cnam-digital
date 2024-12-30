package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.DedupeMatch
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.ExternalReference
import civ.cnam.enrolment.domain.model.value.attachment.FingerprintsAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.PhotoAttachmentId
import civ.cnam.enrolment.domain.model.value.attachment.SignatureAttachmentId
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.data.model.value.MobileNumber
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import dev.dry.core.jpa.converter.ValueClassNullableLongAttributeConverter
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version
import java.time.LocalDateTime

@Entity(name = "PartialEnrolment")
@Table(uniqueConstraints = [UniqueConstraint(name = "uc_partial_enrolment_id", columnNames = ["enrolmentId"])])
class PartialEnrolmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Convert(converter = ValueClassLongAttributeConverter::class)
    override val id: PartialEnrolment.ID,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val mobileNumber: MobileNumber,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val enrolmentId: EnrolmentId,

    @Column(nullable = false, updatable = false)
    override val startedAt: LocalDateTime,

    @Column
    override var completedAt: LocalDateTime?,

    @Column
    override var verifiedAt: LocalDateTime?,

    @Column
    override var processedAt: LocalDateTime?,

    @Column(nullable = false)
    override var verificationTaskPendingCount: Int,

    @Column(nullable = false)
    override var correctiveActionPendingCount: Int,

    @Column(nullable = false)
    override var reviewTaskPendingCount: Int,

    /**
     * The final approval status
     */
    @Enumerated
    override var approvalStatus: PartialEnrolment.ApprovalStatus?,

    @JsonIgnore
    @JoinColumn(
        name = DEDUPE_MATCH_JOIN_COLUMN,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_enrolment_dedupe_match"),
    )
    @ManyToOne(fetch = FetchType.LAZY)
    override var dedupeMatch: DedupeMatchEntity?,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "enrolment")
    override var identityDocument: IdentityDocumentEntity?,

    @Column
    @JsonIgnore
    var identityDocumentOcrSucceeded: Boolean? = null,

    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var fingerprintsAttachmentId: FingerprintsAttachmentId?,

    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var photoAttachmentId: PhotoAttachmentId?,

    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var signatureAttachmentId: SignatureAttachmentId?,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "enrolment")
    override val supportingDocuments: MutableList<SupportingDocumentEntity>,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "enrolment")
    override var enrolmentDetails: EnrolmentDetailsEntity?,

    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var externalReference: ExternalReference? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "enrolment")
    override val actions: MutableList<EnrolmentActionEntity> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "enrolment")
    override val verificationChecks: MutableList<VerificationOutboxTaskEntity> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "enrolment")
    override val reviewTasks: MutableList<ReviewTaskEntity> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "enrolment")
    override val correctiveActions: MutableList<CorrectiveActionEntity> = mutableListOf(),
) : PartialEnrolment {
    @Column(name = DEDUPE_MATCH_JOIN_COLUMN, nullable = true, insertable = false, updatable = false)
    @Convert(converter = ValueClassNullableLongAttributeConverter::class)
    var dedupeMatchId: DedupeMatch.ID = DedupeMatch.ID.NULL

    @Version
    val version: Int = 0

    companion object {
        const val DEDUPE_MATCH_JOIN_COLUMN = "dedupe_match_id"
    }
}
