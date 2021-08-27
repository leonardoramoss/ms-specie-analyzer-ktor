package io.specie.analyzer.application.domain.specie.analyzer

import io.specie.analyzer.application.domain.specie.SpecieAnalysis

interface Analyzer {
    fun analyze(specieAnalysis: SpecieAnalysis): SpecieAnalysis
}
