package io.specie.analyzer.application.spi

fun interface Fetcher<in T, out R> {
    fun fetch(argument: T?): R?
}
