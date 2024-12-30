package civ.cnam.zetes.api.client.exception

import org.http4k.core.Status

class ZetesCreateEnrolmentFailedException private constructor(
    message: String,
    cause: Throwable?,
) : ZetesException(message, cause) {

    constructor(id: String, status: Status): this(
        "zetes create enrolment operation failed for request id '$id' with API response status $status",
        null,
    )

    constructor(id: String): this("zetes create enrolment operation failed for request id '$id'", null)
}
