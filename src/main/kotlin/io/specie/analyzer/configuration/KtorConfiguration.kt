package io.specie.analyzer.configuration

import com.fasterxml.jackson.core.JsonParseException
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respond
import io.specie.analyzer.application.domain.specie.analyzer.exception.SpecieAnalysisException
import io.specie.analyzer.application.domain.specie.analyzer.exception.SpecieAnalysisValidationException
import org.slf4j.event.Level
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.ktorConfiguration() {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson()
    }

    install(StatusPages) {
        exception<Exception> { call.respondFailure(InternalServerError, it) }
        exception<JsonParseException> { call.respondFailure(BadRequest, it) }
        exception<NullPointerException> { call.respondFailure(BadRequest, it) }
        exception<SpecieAnalysisValidationException> { call.respondFailure(BadRequest, it) }
    }
}

suspend inline fun ApplicationCall.respondFailure(status: HttpStatusCode, exception: Throwable) {
    response.status(status)

    val cause = when (exception) {
        is SpecieAnalysisException -> exception.reason
        else -> exception.cause.toString()
    }

    respond(
        mapOf(
            "code" to status.value,
            "cause" to cause,
            "message" to (exception.cause.takeIf { it != null }?.message ?: exception.message),
            "timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        )
    )
}
