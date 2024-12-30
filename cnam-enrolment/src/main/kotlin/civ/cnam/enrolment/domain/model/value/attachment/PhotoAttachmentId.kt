package civ.cnam.enrolment.domain.model.value.attachment

import dev.dry.core.data.mapper.BidirectionalDataMapper

@JvmInline
value class PhotoAttachmentId(val value: String) {
    companion object {
        val mapper = BidirectionalDataMapper(::PhotoAttachmentId, PhotoAttachmentId::value)
    }
}
