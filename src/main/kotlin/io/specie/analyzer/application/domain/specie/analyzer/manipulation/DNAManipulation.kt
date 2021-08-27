package io.specie.analyzer.application.domain.specie.analyzer.manipulation

import io.specie.analyzer.application.domain.specie.DNA
import java.util.concurrent.atomic.AtomicLong

class DNAManipulation(private val dna: Array<DNA>) {

    /**
     * Given an array of strings this method will transpose from row to column values
     *
     * Original    Transposed
     * A A A A     A B C D
     * B B B B     A B C D
     * C C C C     A B D D
     * D D D D     A B C D
     *
     * @return transposed array
     */
    fun transposeDNASequence(): Array<DNA> =
        dna.explodeValues()
            .transposeRowToColumn()
            .multidimensionalCharToStringArray()

    /**
     * Given an array of strings this method will transpose diagonal from left to right for row values
     *
     * Original    Transposed
     * A B C D         A
     * B C D A        B B
     * C D A B       C C C
     * D A B C      D D D D
     *               A A A
     *                B B
     *                 C
     *
     * @return transposed array
     */
    fun transposeDiagonalDNASequence(): Array<DNA> =
        dna.explodeValues()
            .diagonalMultidimensionalCharToArrayString()

    /**
     * Given an array of strings this method will transpose diagonal from right to left for row values
     *
     * Original    Transposed
     * A B C D         D
     * B C D A        A C
     * C D A B       B D B
     * D A B C      C A C A
     *               B D B
     *                A C
     *                 D
     *
     * @return transposed array
     */
    fun transposeReversedDiagonalDNASequence(): Array<DNA> =
        dna.reverseStringArrayValues().explodeValues()
            .diagonalMultidimensionalCharToArrayString()
}

fun Array<String>.explodeValues(): Array<Array<Char>> = this.map { it.toCharArray().toTypedArray() }.toTypedArray()

fun Array<String>.reverseStringArrayValues(): Array<String> = this.map { it.reversed() }.toTypedArray()

fun Array<Array<Char>>.multidimensionalCharToStringArray(): Array<String> {
    val builder = StringBuilder()
    val delimiter = "-"

    for (i in this.indices) {
        builder.append(this[i].joinToString(""))
        if (i != this.size - 1) {
            builder.append(delimiter)
        }
    }

    return builder.toString().split(delimiter).toTypedArray()
}

fun Array<Array<Char>>.transposeRowToColumn(): Array<Array<Char>> {

    val transposeMatrix = Array(this.size) { CharArray(this[0].size).toTypedArray() }

    for (i in this.indices) {
        for (j in this[i].indices) {
            transposeMatrix[j][i] = this[i][j]
        }
    }

    return transposeMatrix
}

fun Array<Array<Char>>.diagonalMultidimensionalCharToArrayString(): Array<String> {

    val length: Int = this.size
    val delimiter = "-"
    val diagonalLines = length + length - 1
    val midPoint = diagonalLines / 2 + 1
    val output = StringBuilder()
    val itemsInDiagonal = AtomicLong()

    for (i in 1..diagonalLines) {
        val items = StringBuilder()
        var rowIndex: Int
        var columnIndex: Int

        if (i <= midPoint) {
            itemsInDiagonal.incrementAndGet()
            for (j in 0 until itemsInDiagonal.get()) {
                rowIndex = (i - j - 1).toInt()
                columnIndex = j.toInt()
                items.append(this[rowIndex][columnIndex])
            }
        } else {
            itemsInDiagonal.decrementAndGet()
            for (j in 0 until itemsInDiagonal.get()) {
                rowIndex = (length - 1 - j).toInt()
                columnIndex = (i - length + j).toInt()
                items.append(this[rowIndex][columnIndex])
            }
        }

        if (i <= diagonalLines) {
            output.append(items)
            if (i <= diagonalLines - 1) {
                output.append(delimiter)
            }
        }
    }

    return output.toString().split(delimiter).toTypedArray()
}
