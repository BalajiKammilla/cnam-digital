package civ.cnam.enrolment.adaptor.model.entity

import civ.cnam.enrolment.domain.model.entity.DedupeMatch
import civ.cnam.enrolment.domain.model.value.personal.FirstName
import civ.cnam.enrolment.domain.model.value.personal.LastName
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.value.document.DocumentNumber
import dev.dry.core.jpa.converter.ValueClassLongAttributeConverter
import dev.dry.core.jpa.converter.ValueClassStringAttributeConverter
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity(name = "DedupeMatch")
class DedupeMatchEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Convert(converter = ValueClassLongAttributeConverter::class)
    override val id: DedupeMatch.ID = DedupeMatch.ID.NULL,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val firstName: FirstName,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val lastName: LastName,

    @Column(nullable = false, updatable = false)
    override val dateOfBirth: LocalDate,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val identityDocumentTypeCode: DocumentTypeCode,

    @Column(nullable = false, updatable = false)
    @Convert(converter = ValueClassStringAttributeConverter::class)
    override val identityDocumentNumber: DocumentNumber,
) : DedupeMatch {
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST], mappedBy = "dedupeMatch")
    val enrolments: MutableList<PartialEnrolmentEntity> = mutableListOf()
}
