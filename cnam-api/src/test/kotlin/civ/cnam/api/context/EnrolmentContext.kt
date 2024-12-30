package civ.cnam.api.context

object EnrolmentContext {
    var enrolmentId: String?
        get() = ctx[ENROLMENT_ID]
        set(enrolmentId) {
            ctx[ENROLMENT_ID] = enrolmentId
        }
    var fingerprintAttachmentId: String?
        get() = ctx[FINGERPRINT_ATTACHMENT_ID]
        set(fingerprintAttachmentId) {
            ctx[FINGERPRINT_ATTACHMENT_ID] = fingerprintAttachmentId
        }

    private val ctx = ScenarioContext
    private const val ENROLMENT_ID = "ENROLMENT_ID"
    private const val FINGERPRINT_ATTACHMENT_ID = "FINGERPRINT_ATTACHMENT_ID"
}
