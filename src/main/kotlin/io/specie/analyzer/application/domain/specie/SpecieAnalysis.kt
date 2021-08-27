package io.specie.analyzer.application.domain.specie

import io.specie.analyzer.application.domain.specie.analyzer.exception.SpecieAnalysisValidationException
import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

typealias DNA = String

data class SpecieAnalysis(
    val uuid: UUID,
    val dna: DNA,
    var expectedIdentifier: SpecieIdentifier? = null,
    var identifier: SpecieIdentifier? = SpecieIdentifier.NOT_IDENTIFIED,
    var analyzedAt: LocalDateTime? = null
) {

    companion object {

        @JvmField
        val ALLOWED_NITROGENOUS_BASE: Pattern = Pattern.compile("[ATCG]+")

        private const val MINIMUM_NxN_LENGTH = 4
        const val DELIMITER = "-"

        operator fun invoke(
            dna: Array<DNA>,
            expectedIdentifier: SpecieIdentifier? = null,
            identifier: SpecieIdentifier? = null
        ): SpecieAnalysis {
            dna.let {
                if (dna.isEmpty().not() && it.size >= MINIMUM_NxN_LENGTH) {
                    checkAllowedNitrogenousBase(it)
                    checkDNAStructure(it)

                    val dnaChain = it.joinToString(DELIMITER)

                    return SpecieAnalysis(
                        UUID.nameUUIDFromBytes(dnaChain.toByteArray()),
                        dnaChain,
                        expectedIdentifier ?: identifier,
                        identifier
                    )
                }
            }

            throw SpecieAnalysisValidationException("DNA sequence ${dna.joinToString()} is invalid.")
        }

        private fun checkAllowedNitrogenousBase(dna: Array<DNA>) {
            dna.forEach {
                if (ALLOWED_NITROGENOUS_BASE.matcher(it).matches().not())
                    throw SpecieAnalysisValidationException("DNA sequence $it in ${dna.joinToString()} is not valid.")
            }
        }

        private fun checkDNAStructure(dna: Array<DNA>) {
            val sequenceLength = dna.size
            dna.forEach {
                if (sequenceLength != it.length)
                    throw SpecieAnalysisValidationException("There is not a NxN DNA sequence.")
            }
        }
    }

    /**
     *
     */
    fun originalDNA(): Array<DNA> =
        this.dna.split(DELIMITER).toTypedArray()

    /**
     *
     */
    fun markExpectedIdentifierAs(specieIdentifier: SpecieIdentifier): SpecieAnalysis {
        this.expectedIdentifier = specieIdentifier
        return this
    }

    /**
     *
     */
    fun markIdentifiedAs(specieIdentifier: SpecieIdentifier): SpecieAnalysis {
        this.identifier = specieIdentifier
        this.analyzedAt = LocalDateTime.now()
        return this
    }

    /**
     *
     */
    fun isIdentifierMatchesAsExpected(): Boolean {
        if (this.analyzedAt != null &&
            this.expectedIdentifier == null && this.identifier != null
        ) {
            return true
        }
        if (this.identifier == null) {
            return false
        }
        return expectedIdentifier == identifier
    }
}
