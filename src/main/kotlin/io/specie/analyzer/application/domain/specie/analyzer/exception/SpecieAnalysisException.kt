package io.specie.analyzer.application.domain.specie.analyzer.exception

open class SpecieAnalysisException(message: String, val reason: String) : RuntimeException(message)
