package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.random.Random

class RopeByteStringIntensiveBalanceTest {

    @Test
    fun testIntensiveConcatenation_balanced() {
        val initialData = ByteArray(100) { it.toByte() }
        var result = RopeByteString(data = initialData, maintainBalance = true)

        repeat(1000) { i ->
            val newData = ByteArray(50) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = true)

            if (i % 100 == 0) {
                assertEquals(100 + (i + 1) * 50, result.size)

                val randomIndex = Random.nextInt(result.size)
                val byteArray = result.toByteArray()
                assertEquals(byteArray[randomIndex], result[randomIndex])

                val start = result.size / 4
                val end = result.size * 3 / 4
                val substring = result.substring(start, end)
                assertContentEquals(
                    byteArray.copyOfRange(start, end),
                    substring.toByteArray()
                )
            }
        }

        assertEquals(100 + 1000 * 50, result.size)
    }

    @Test
    fun testIntensiveConcatenation_unbalanced() {
        val initialData = ByteArray(100) { it.toByte() }
        var result = RopeByteString(data = initialData, maintainBalance = false)

        repeat(1000) { i ->
            val newData = ByteArray(50) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = false)

            if (i % 100 == 0) {
                assertEquals(100 + (i + 1) * 50, result.size)

                val randomIndex = Random.nextInt(result.size)
                val byteArray = result.toByteArray()
                assertEquals(byteArray[randomIndex], result[randomIndex])

                val start = result.size / 4
                val end = result.size * 3 / 4
                val substring = result.substring(start, end)
                assertContentEquals(
                    byteArray.copyOfRange(start, end),
                    substring.toByteArray()
                )
            }
        }

        assertEquals(100 + 1000 * 50, result.size)
    }

    @Test
    fun testMixedOperations_balanced() {
        val initialData = ByteArray(1000) { it.toByte() }
        var result = RopeByteString(data = initialData, maintainBalance = true)

        repeat(100) { i ->
            val newData = ByteArray(50) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = true)
            result = result.substring(25, result.size - 25)
            result += RopeByteString(data = ByteArray(75) { it.toByte() }, maintainBalance = true)

            if (i % 10 == 0) {
                val byteArray = result.toByteArray()
                val randomIndex = Random.nextInt(result.size)

                assertEquals(byteArray[randomIndex], result[randomIndex])

                val start = result.size / 3
                val end = result.size * 2 / 3
                val substring = result.substring(start, end)
                assertContentEquals(
                    byteArray.copyOfRange(start, end),
                    substring.toByteArray()
                )
            }
        }
    }

    @Test
    fun testMixedOperations_unbalanced() {
        val initialData = ByteArray(1000) { it.toByte() }
        var result = RopeByteString(data = initialData, maintainBalance = false)

        repeat(100) { i ->
            val newData = ByteArray(50) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = false)
            result = result.substring(25, result.size - 25)
            result += RopeByteString(data = ByteArray(75) { it.toByte() }, maintainBalance = false)

            if (i % 10 == 0) {
                val byteArray = result.toByteArray()
                val randomIndex = Random.nextInt(result.size)

                assertEquals(byteArray[randomIndex], result[randomIndex])

                val start = result.size / 3
                val end = result.size * 2 / 3
                val substring = result.substring(start, end)
                assertContentEquals(
                    byteArray.copyOfRange(start, end),
                    substring.toByteArray()
                )
            }
        }
    }

    @Test
    fun testConcatenationWithMixedSizes_balanced() {
        var result = RopeByteString()

        repeat(50) { i ->
            val size = when {
                i % 3 == 0 -> 10
                i % 3 == 1 -> 1000
                else -> 5000
            }

            val newData = ByteArray(size) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = true)

            if (i % 3 == 2) {
                val byteArray = result.toByteArray()
                listOf(0, result.size / 2, result.size - 1).forEach { pos ->
                    assertEquals(byteArray[pos], result[pos])
                }
            }
        }
    }

    @Test
    fun testConcatenationWithMixedSizes_unbalanced() {
        var result = RopeByteString()

        repeat(50) { i ->
            val size = when {
                i % 3 == 0 -> 10
                i % 3 == 1 -> 1000
                else -> 5000
            }

            val newData = ByteArray(size) { (it + i).toByte() }
            result += RopeByteString(data = newData, maintainBalance = false)

            if (i % 3 == 2) {
                val byteArray = result.toByteArray()
                listOf(0, result.size / 2, result.size - 1).forEach { pos ->
                    assertEquals(byteArray[pos], result[pos])
                }
            }
        }
    }
}