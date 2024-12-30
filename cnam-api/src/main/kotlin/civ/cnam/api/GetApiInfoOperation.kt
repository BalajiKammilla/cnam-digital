package civ.cnam.api

import dev.dry.core.configuration.BuildInfo
import dev.dry.core.configuration.SystemInfo
import dev.dry.core.jaxrs.response.DefaultApiCompletedResponse
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse

@Singleton
@Path("/info")
class GetApiInfoOperation(private val buildInfo: BuildInfo, private val systemInfo: SystemInfo) {
    @GET
    @Operation(summary = "Retrieve API Info.", hidden = true)
    @APIResponse(
        responseCode = "200",
        description = "API Info retrieved.",
        content = [
            Content(
                mediaType = APPLICATION_JSON,
                schema = Schema(implementation = GetApiInfoResponse::class)
            )
        ]
    )
    @Produces(APPLICATION_JSON)
    operator fun invoke(): ApiInfo {
        return ApiInfo(
            version = buildInfo.version(),
            timezone = systemInfo.timezone(),
        )
    }

    class ApiInfo(val version: String, val timezone: String)
    class GetApiInfoResponse(
        data: ApiInfo,
    ) : DefaultApiCompletedResponse<ApiInfo>(data)
}
