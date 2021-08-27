package io.specie.analyzer.configuration

import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.specie.analyzer.configuration.database.DatabaseAssertion
import io.specie.analyzer.configuration.database.DatabaseFixture
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTest(val applicationModule: Application.() -> Unit) : DatabaseAssertion by DatabaseFixture() {

    private val engine = TestApplicationEngine(createTestEnvironment())

    @BeforeAll
    internal fun startEnviroment() {
        engine.apply { applicationModule(application) }
        engine.start()
    }

    @AfterAll
    internal fun stopEnviroment() {
        engine.stop(0, 0)
    }

    internal fun post(uri: String, payload: String, assertion: TestApplicationResponse.() -> Unit = { TODO() }) = withTestEngine {
        handleRequest(HttpMethod.Post, uri) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }.apply { assertion(response) }
    }

    internal fun get(uri: String, assertion: TestApplicationResponse.() -> Unit = { TODO() }) = withTestEngine {
        handleRequest(HttpMethod.Get, uri) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }.apply { assertion(response) }
    }

    private fun <R> withTestEngine(test: TestApplicationEngine.() -> R): R = engine.test()
}
