package io.specie.analyzer.application.domain.event

import io.specie.analyzer.application.domain.specie.SpecieAnalysis

open class DomainEvent(private val value: Any) : Event {

    @Suppress("unchecked")
    override fun <T> getSource(): T = value as T

    data class SpecieAnalyzed(private val specieAnalysis: SpecieAnalysis) : DomainEvent(specieAnalysis)
}
