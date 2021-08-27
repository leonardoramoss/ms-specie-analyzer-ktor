package io.specie.analyzer.configuration

import io.ktor.application.Application
import io.specie.analyzer.application.domain.event.EventBus
import io.specie.analyzer.application.domain.event.SpecieAnalyzedListener
import org.koin.ktor.ext.get

fun Application.eventConfiguration(): EventBus {

    val eventBus: EventBus = get()

    return eventBus
        .register(SpecieAnalyzedListener(get()))
}
