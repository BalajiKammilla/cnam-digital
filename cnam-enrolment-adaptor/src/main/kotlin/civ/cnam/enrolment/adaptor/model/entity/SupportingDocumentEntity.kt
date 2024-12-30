package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.PartialEnrolment
import civ.cnam.enrolment.domain.model.entity.SupportingDocument
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import com.fasterxml.jackson.annotation.JsonIgnore
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
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
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity(name = "SupportingDocument")
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_supporting_document_attachment_id",
            columnNames = ["documentAttachmentId"]
        )
    ]
)
class SupportingDocumentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    override val id: SupportingDocument.ID,

    @JoinColumn(
        name = ENROLMENT_JOIN_COLUMN,
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_supporting_document_enrolment"),
        nullable = false,
        updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    override val enrolment: PartialEnrolmentEntity,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val documentAttachmentId: DocumentAttachmentId,

    @Column(nullable = false, updatable = false)
    @Enumerated
    override val purpose: SupportingDocument.Purpose,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val documentTypeCode: DocumentTypeCode,

    @Column(nullable = false, updatable = false)
    override val pageCount: Int
) : SupportingDocument {
    @Column(name = ENROLMENT_JOIN_COLUMN, nullable = false, insertable = false, updatable = false)
    val enrolmentId: PartialEnrolment.ID = PartialEnrolment.ID.NULL

    companion object {
        const val ENROLMENT_JOIN_COLUMN = "enrolment_id"
    }
}
