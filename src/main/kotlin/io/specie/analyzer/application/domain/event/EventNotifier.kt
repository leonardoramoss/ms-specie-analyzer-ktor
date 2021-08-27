package io.specie.analyzer.application.domain.event

fun interface EventNotifier {
    fun notify(event: Event)
}
