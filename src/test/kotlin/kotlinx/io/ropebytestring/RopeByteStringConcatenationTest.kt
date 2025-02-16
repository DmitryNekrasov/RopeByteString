package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RopeByteStringConcatenationTest {

    @Test
    fun testConcatenation_emptyStrings() {
        val empty1 = RopeByteString()
        val empty2 = RopeByteString()
        val result = empty1 + empty2

        assertTrue(result.isEmpty())
        assertEquals(0, result.size)
    }

    @Test
    fun testConcatenation_emptyWithNonEmpty() {
        val empty = RopeByteString()
        val nonEmpty = RopeByteString(1, 2, 3)

        val result1 = empty + nonEmpty
        assertEquals(nonEmpty.toString(), result1.toString())

        val result2 = nonEmpty + empty
        assertEquals(nonEmpty.toString(), result2.toString())
    }

    @Test
    fun testConcatenation_smallStrings() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val result = rope1 + rope2

        assertEquals(6, result.size)
        assertEquals("RopeByteString(size=6 hex=010203040506)", result.toString())
    }

    @Test
    fun testConcatenation_multipleOperations() {
        val rope1 = RopeByteString(1)
        val rope2 = RopeByteString(2)
        val rope3 = RopeByteString(3)
        val rope4 = RopeByteString(4)

        val result = rope1 + rope2 + rope3 + rope4
        assertEquals(4, result.size)
        assertEquals("RopeByteString(size=4 hex=01020304)", result.toString())
    }

    @Test
    fun testConcatenation_withChunks() {
        val chunkSize = RopeByteString.DEFAULT_CHUNK_SIZE
        val data1 = ByteArray(chunkSize - 10) { it.toByte() }
        val data2 = ByteArray(chunkSize - 10) { (it + chunkSize).toByte() }

        val rope1 = RopeByteString(data1)
        val rope2 = RopeByteString(data2)
        val result = rope1 + rope2

        assertEquals(data1.size + data2.size, result.size)

        for (i in data1.indices) {
            assertEquals(data1[i], result[i])
        }
        for (i in data2.indices) {
            assertEquals(data2[i], result[i + data1.size])
        }
    }

    @Test
    fun testConcatenation_largeStrings() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val data1 = ByteArray(size) { it.toByte() }
        val data2 = ByteArray(size) { (it + size).toByte() }

        val rope1 = RopeByteString(data1)
        val rope2 = RopeByteString(data2)
        val result = rope1 + rope2

        assertEquals(size * 2, result.size)

        for (i in data1.indices) {
            assertEquals(data1[i], result[i])
        }
        for (i in data2.indices) {
            assertEquals(data2[i], result[i + size])
        }
    }

    @Test
    fun testConcatenation_differentChunkSizes() {
        val data1 = ByteArray(100) { it.toByte() }
        val data2 = ByteArray(100) { (it + 100).toByte() }

        val rope1 = RopeByteString(data = data1, chunkSize = 30)
        val rope2 = RopeByteString(data = data2, chunkSize = 40)
        val result = rope1 + rope2

        assertEquals(200, result.size)

        for (i in data1.indices) {
            assertEquals(data1[i], result[i])
        }
        for (i in data2.indices) {
            assertEquals(data2[i], result[i + 100])
        }
    }

    @Test
    fun testConcatenation_withSubstrings() {
        val original1 = RopeByteString(1, 2, 3, 4, 5)
        val original2 = RopeByteString(6, 7, 8, 9, 10)

        val sub1 = original1.substring(1, 4)  // [2, 3, 4]
        val sub2 = original2.substring(1, 4)  // [7, 8, 9]

        val result = sub1 + sub2
        assertEquals(6, result.size)
        assertEquals("RopeByteString(size=6 hex=020304070809)", result.toString())
    }

    @Test
    fun testConcatenation_maxSizedChunks() {
        val maxChunkSize = RopeByteString.MAX_CHUNK_SIZE
        val data1 = ByteArray(maxChunkSize) { it.toByte() }
        val data2 = ByteArray(maxChunkSize) { (it + maxChunkSize).toByte() }

        val rope1 = RopeByteString(data = data1, chunkSize = maxChunkSize)
        val rope2 = RopeByteString(data = data2, chunkSize = maxChunkSize)
        val result = rope1 + rope2

        assertEquals(maxChunkSize * 2, result.size)
        assertEquals(data1[maxChunkSize - 1], result[maxChunkSize - 1])
        assertEquals(data2[0], result[maxChunkSize])
    }

    @Test
    fun testConcatenation_complexScenario() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(data = ByteArray(1000) { 4 }, chunkSize = 100)
        val rope3 = RopeByteString(5, 6, 7).substring(1, 3)  // [6, 7]

        val result = rope1 + rope2 + rope3

        assertEquals(1005, result.size)
        assertTrue(result.startsWith(rope1))
        assertTrue(result.endsWith(rope3))
    }

    @Test
    fun testConcatenation_smallStringsCycle() {
        var result = RopeByteString(1)
        val expectedSize = 10

        for (i in 2..expectedSize) {
            result += RopeByteString(i.toByte())
        }

        assertEquals(expectedSize, result.size)
        for (i in 1..expectedSize) {
            assertEquals(i.toByte(), result[i - 1])
        }
    }

    @Test
    fun testConcatenation_increasingSizeCycle() {
        var result = RopeByteString()
        val iterationCount = 5

        for (i in 1..iterationCount) {
            val newData = ByteArray(i * 100) { it.toByte() }
            result += RopeByteString(*newData)
        }

        val expectedSize = (iterationCount * (iterationCount + 1) / 2) * 100
        assertEquals(expectedSize, result.size)
    }

    @Test
    fun testConcatenation_chunkBoundaryCycle() {
        val chunkSize = RopeByteString.DEFAULT_CHUNK_SIZE
        var result = RopeByteString()

        val iterationCount = 5
        val singleSize = chunkSize - 10

        for (i in 0..<iterationCount) {
            val newData = ByteArray(singleSize) { (it + i * singleSize).toByte() }
            result += RopeByteString(*newData)
        }

        assertEquals(singleSize * iterationCount, result.size)

        for (i in 0..<iterationCount) {
            val index = (i * singleSize) + (singleSize - 1)
            if (index < result.size) {
                assertEquals(((singleSize - 1) + i * singleSize).toByte(), result[index])
            }
        }
    }

    @Test
    fun testConcatenation_alternatingSmallLargeCycle() {
        var result = RopeByteString()
        val iterationCount = 5

        for (i in 0..<iterationCount) {
            result += RopeByteString(i.toByte(), (i + 1).toByte(), (i + 2).toByte())

            val largeData = ByteArray(1000) { (it + i).toByte() }
            result += RopeByteString(largeData)
        }

        val expectedSize = iterationCount * (3 + 1000)
        assertEquals(expectedSize, result.size)
    }

    @Test
    fun testConcatenation_largeStringsCycle() {
        var result = RopeByteString()
        val iterationCount = 3
        val singleSize = RopeByteString.DEFAULT_CHUNK_SIZE * 2

        for (i in 0..<iterationCount) {
            val newData = ByteArray(singleSize) { (it + i).toByte() }
            result += RopeByteString(*newData)
        }

        assertEquals(singleSize * iterationCount, result.size)

        for (i in 0..<iterationCount) {
            val index = i * singleSize
            assertEquals(i.toByte(), result[index])
        }
    }

    @Test
    fun testConcatenation_mixedOperationsCycle() {
        var result = RopeByteString(1, 2, 3)
        val iterationCount = 5

        for (i in 0..<iterationCount) {
            result += RopeByteString(4, 5)

            result = result.substring(1, result.size)

            val mediumData = ByteArray(100) { it.toByte() }
            result += RopeByteString(*mediumData)
        }

        val expectedFinalSize = 508
        assertEquals(expectedFinalSize, result.size)
    }

    @Test
    fun testConcatenation_emptyStringsCycle() {
        var result = RopeByteString(1, 2, 3)
        val iterationCount = 100

        for (i in 0..<iterationCount) {
            result += RopeByteString()
        }

        assertEquals(3, result.size)
        assertEquals("RopeByteString(size=3 hex=010203)", result.toString())
    }

    @Test
    fun testConcatenation_growingPatternCycle() {
        var result = RopeByteString()
        val pattern = RopeByteString(1, 2, 3)
        val iterationCount = 5

        for (i in 1..iterationCount) {
            var iterationResult = pattern
            for (j in 1..<i) {
                iterationResult += pattern
            }
            result += iterationResult
        }

        val expectedSize = pattern.size * (iterationCount * (iterationCount + 1) / 2)
        assertEquals(expectedSize, result.size)
    }

    @Test
    fun testConcatenation_maxChunksCycle() {
        var result = RopeByteString()
        val iterationCount = 3
        val chunkSize = RopeByteString.MAX_CHUNK_SIZE

        for (i in 0..<iterationCount) {
            val newData = ByteArray(chunkSize) { (it + i).toByte() }
            result += RopeByteString(data = newData, chunkSize = chunkSize)
        }

        assertEquals(chunkSize * iterationCount, result.size)

        for (i in 0..<iterationCount) {
            val boundaryIndex = chunkSize * i
            assertEquals((i).toByte(), result[boundaryIndex])
        }
    }
}