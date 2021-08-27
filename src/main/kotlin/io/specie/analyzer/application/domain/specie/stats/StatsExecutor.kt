package io.specie.analyzer.application.domain.specie.stats

interface StatsExecutor<T> {

    fun execute(): T
}
