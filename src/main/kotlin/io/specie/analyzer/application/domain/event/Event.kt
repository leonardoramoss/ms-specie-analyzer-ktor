package io.specie.analyzer.application.domain.event

interface Event {

    fun <T> getSource(): T
}
