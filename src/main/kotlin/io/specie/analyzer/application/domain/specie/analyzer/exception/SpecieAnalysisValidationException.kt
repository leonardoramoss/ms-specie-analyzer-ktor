package io.specie.analyzer.application.domain.specie.analyzer.exception

class SpecieAnalysisValidationException(message: String) :
    SpecieAnalysisException(message, "Validation error")
