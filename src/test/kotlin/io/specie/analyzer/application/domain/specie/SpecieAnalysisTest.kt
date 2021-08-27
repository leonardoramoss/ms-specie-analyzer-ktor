package io.specie.analyzer.application.domain.specie

import io.specie.analyzer.application.domain.specie.analyzer.exception.SpecieAnalysisValidationException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("Specie Analysis")
internal class SpecieAnalysisTest {

    private val SIMIAN_DNA_SEQUENCE = arrayOf("ATCGCA", "TCTCCG", "TGGTTG", "CCTTTC", "GTAATC", "ACCACT")
    private val HUMAN_DNA_SEQUENCE = arrayOf("ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG")
    private val NOT_MINIMUM_NxN_DNA_SEQUENCE = arrayOf("ATC", "GCC", "ACC")
    private val NOT_A_NxN_DNA_SEQUENCE = arrayOf("ATCA", "GCCC", "TACC", "AAGC", "TTTTA")
    private val NOT_A_VALID_DNA_SEQUENCE_U_IS_NOT_VALID = arrayOf("ATCA", "GCCC", "TACC", "AUCA")

    val EXCEPTION_MESSAGE_DNA_NOT_VALID = { value: Any? -> "DNA sequence $value is invalid." }
    val EXCEPTION_MESSAGE_NOT_A_NxN_SEQUENCE = "There is not a NxN DNA sequence."
    val EXCEPTION_MESSAGE_PART_OF_DNA_SEQUENCE_NOT_VALID: (Any, Any) -> String =
        { value1, value2 -> "DNA sequence $value1 in $value2 is not valid." }

    @Test
    fun `given a simian DNA and identifier marked as simian should be all matches`() {
        // "c0d7dab2-e85b-3029-94f7-0cb598b4e3e0"
        val expectedUUID = UUID.nameUUIDFromBytes(SIMIAN_DNA_SEQUENCE.joinToString("-").toByteArray())

        val speciesAnalysis =
            SpecieAnalysis(dna = SIMIAN_DNA_SEQUENCE)
                .markIdentifiedAs(SpecieIdentifier.SIMIAN)

        assertAll(
            { assertEquals(SIMIAN_DNA_SEQUENCE.joinToString(), speciesAnalysis.originalDNA().joinToString()) },
            { assertNull(speciesAnalysis.expectedIdentifier) },
            { assertEquals(SpecieIdentifier.SIMIAN, speciesAnalysis.identifier) },
            { assertEquals(expectedUUID.toString(), speciesAnalysis.uuid.toString()) }
        )
    }

    @Test
    fun `given a human DNA and identifier marked as human should be all matches`() {
        // "053a06a4-5b45-3e69-8f1c-cadf36bd0950"
        val expectedUUID = UUID.nameUUIDFromBytes(HUMAN_DNA_SEQUENCE.joinToString("-").toByteArray())

        val speciesAnalysis =
            SpecieAnalysis(dna = HUMAN_DNA_SEQUENCE)
                .markIdentifiedAs(SpecieIdentifier.HUMAN)

        assertAll(
            { assertEquals(HUMAN_DNA_SEQUENCE.joinToString(), speciesAnalysis.originalDNA().joinToString()) },
            { assertNull(speciesAnalysis.expectedIdentifier) },
            { assertEquals(SpecieIdentifier.HUMAN, speciesAnalysis.identifier) },
            { assertEquals(expectedUUID.toString(), speciesAnalysis.uuid.toString()) }
        )
    }

    @Test
    fun `given a human DNA, when expected identifier marked simian and has identifier marked as human, should be matches return false`() {
        val speciesAnalysis =
            SpecieAnalysis(dna = HUMAN_DNA_SEQUENCE)
                .markExpectedIdentifierAs(SpecieIdentifier.SIMIAN)
                .markIdentifiedAs(SpecieIdentifier.HUMAN)

        assertFalse(speciesAnalysis.isIdentifierMatchesAsExpected())
    }

    @Test
    fun `given a simian DNA, when expected identifier marked human and has identifier marked as simian, should be matches return false`() {
        val speciesAnalysis =
            SpecieAnalysis(dna = SIMIAN_DNA_SEQUENCE)
                .markExpectedIdentifierAs(SpecieIdentifier.HUMAN)
                .markIdentifiedAs(SpecieIdentifier.SIMIAN)

        assertFalse(speciesAnalysis.isIdentifierMatchesAsExpected())
    }

    @Test
    fun `given a human DNA, when expected identifier marked human and has identifier marked as human, should be matches return true`() {
        val speciesAnalysis =
            SpecieAnalysis(dna = HUMAN_DNA_SEQUENCE)
                .markExpectedIdentifierAs(SpecieIdentifier.HUMAN)
                .markIdentifiedAs(SpecieIdentifier.HUMAN)

        assertTrue(speciesAnalysis.isIdentifierMatchesAsExpected())
    }

    @Test
    fun `given a human DNA, when expected identifier marked human and has no identifier marked, should be matches return false`() {
        val speciesAnalysis =
            SpecieAnalysis(dna = HUMAN_DNA_SEQUENCE)
                .markExpectedIdentifierAs(SpecieIdentifier.HUMAN)

        assertFalse(speciesAnalysis.isIdentifierMatchesAsExpected())
    }

    @Test
    fun `given a human DNA, when expected identifier marked human and has no identifier marked, should be matches return false 2`() {
        val speciesAnalysis = SpecieAnalysis(dna = HUMAN_DNA_SEQUENCE)

        assertAll(
            { assertNotNull(speciesAnalysis.uuid) },
            { assertNull(speciesAnalysis.expectedIdentifier) },
            { assertFalse(speciesAnalysis.isIdentifierMatchesAsExpected()) }
        )
    }

    @Nested
    @DisplayName("Exception in specie analysis constructor")
    inner class PrimateAnalyzerConstructorTest {

        @Test
        fun `when given an empty DNA, should be throw SpecieAnalysisValidationException`() {
            val message = EXCEPTION_MESSAGE_DNA_NOT_VALID(arrayOf<DNA>().joinToString())
            assertFailsWith<SpecieAnalysisValidationException>(message) { SpecieAnalysis.invoke(dna = arrayOf()) }
        }

        @Test
        fun `when given DNA don't have the minimum size, should be throw SpecieAnalysisValidationException`() {
            val message = EXCEPTION_MESSAGE_DNA_NOT_VALID(NOT_MINIMUM_NxN_DNA_SEQUENCE.joinToString())
            assertFailsWith<SpecieAnalysisValidationException>(message) { SpecieAnalysis(dna = NOT_MINIMUM_NxN_DNA_SEQUENCE) }
        }

        @Test
        fun `when given DNA is not a NxN, should be SpecieAnalysisValidationException`() {
            val message = EXCEPTION_MESSAGE_NOT_A_NxN_SEQUENCE
            assertFailsWith<SpecieAnalysisValidationException>(message) { SpecieAnalysis(dna = NOT_A_NxN_DNA_SEQUENCE) }
        }

        @Test
        fun `when given a not valid DNA sequence, should be throw SpecieAnalysisValidationException`() {
            val message = EXCEPTION_MESSAGE_PART_OF_DNA_SEQUENCE_NOT_VALID("AUCA", NOT_A_VALID_DNA_SEQUENCE_U_IS_NOT_VALID.joinToString())
            assertFailsWith<SpecieAnalysisValidationException>(message) { SpecieAnalysis(dna = NOT_A_VALID_DNA_SEQUENCE_U_IS_NOT_VALID) }
        }
    }
}
