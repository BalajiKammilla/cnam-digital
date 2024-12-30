package civ.cnam.enrolment.adaptor.configuration

import dev.dry.core.objectstore.ObjectStoreName

object ObjectStoreNames {
    private const val ENROLMENT_OBJECT_STORE_NAME_VALUE = "enrolment"

    val ENROLMENT_OBJECT_STORE_NAME = ObjectStoreName(ENROLMENT_OBJECT_STORE_NAME_VALUE)
}
