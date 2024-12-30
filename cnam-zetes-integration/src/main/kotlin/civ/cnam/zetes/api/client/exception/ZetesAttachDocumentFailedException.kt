package civ.cnam.zetes.api.client.exception

import org.http4k.core.Status

class ZetesAttachDocumentFailedException private constructor(
    message: String,
    cause: Throwable? = null,
) : ZetesException(message, cause) {
    constructor(id: String, documentId: String): this(
        "zetes attach document operation failed for request id '$id' and document id '$documentId'"
    )

    constructor(id: String, documentId: String, status: Status): this(
        "zetes attach document operation failed for request id '$id' and document id '$documentId' with status '$status'"
    )
}
