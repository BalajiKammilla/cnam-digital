package civ.cnam.enrolment.constants

import dev.dry.core.constants.ConfigurationPropertyConstants

object EnrolmentConfigurationProperty : ConfigurationPropertyConstants {
    const val CNAM_ENROLMENT_PROCESSOR_ADAPTOR = "cnam.enrolment.processor.adaptor"
    const val CNAM_PAYER_VERIFICATION_ADAPTOR = "cnam.payer.verification.adaptor"

    const val CNAM_ENROLMENT_OUTBOX_JOB_NAME = "cnam-enrolment-outbox-job"
    const val CNAM_VERIFICATION_OUTBOX_JOB_NAME = "cnam-verification-outbox-job"
    const val CNAM_ENROLMENT_OUTBOX_JOB_ENABLED = "dry.core.scheduling.job.$CNAM_ENROLMENT_OUTBOX_JOB_NAME.enabled"
    const val CNAM_VERIFICATION_OUTBOX_JOB_ENABLED = "dry.core.scheduling.job.$CNAM_VERIFICATION_OUTBOX_JOB_NAME.enabled"

    const val DEFAULT_JPA_OPERATIONS = "default-jpa-operations"
}