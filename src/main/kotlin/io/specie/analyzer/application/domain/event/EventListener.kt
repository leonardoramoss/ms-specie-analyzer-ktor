package io.specie.analyzer.application.domain.event

fun interface EventListener<T> {
    fun onEvent(event: T)
}
