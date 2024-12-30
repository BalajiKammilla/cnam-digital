package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.IdentityDocument
import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity(name = "IdentityDocument")
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_identity_document_attachment_id",
            columnNames = ["documentAttachmentId"]
        )
    ]
)
class IdentityDocumentEntity(
    @JoinColumn(
        name = "id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_identity_document_enrolment"),
    )
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,

    @Id
    @JsonIgnore
    @Convert(converter = ValueClassLongAttributeConverter::class)
    val id: PartialEnrolment.ID = enrolment.id,

    @Column(nullable = false, updatable = true)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var documentNumber: DocumentNumber,

    @Column(nullable = false, updatable = true)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override var documentTypeCode: DocumentTypeCode,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val documentAttachmentId: DocumentAttachmentId,

    @Column(nullable = false, updatable = true)
    override var pageCount: Int,

    @Column(nullable = false, updatable = true)
    override var ocrSucceeded: Boolean,
) : IdentityDocument
