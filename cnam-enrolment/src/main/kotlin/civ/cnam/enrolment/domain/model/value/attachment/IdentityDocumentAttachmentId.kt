package civ.cnam.enrolment.domain.model.value.attachment

@JvmInline
value class IdentityDocumentAttachmentId(val value: String) {
    override fun toString(): String = value

    companion object {
        fun from(documentAttachmentId: DocumentAttachmentId): IdentityDocumentAttachmentId =
            IdentityDocumentAttachmentId(documentAttachmentId.value)
    }
}
