package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RopeByteStringIndexAccessTest {

    @Test
    fun testGet_singleByte() {
        val rope = RopeByteString(0xFF.toByte())
        assertEquals(0xFF.toByte(), rope[0])
    }

    @Test
    fun testGet_multipleBytesWithinChunk() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        assertEquals(1.toByte(), rope[0])
        assertEquals(3.toByte(), rope[2])
        assertEquals(5.toByte(), rope[4])
    }

    @Test
    fun testGet_invalidIndices() {
        val rope = RopeByteString(1, 2, 3)

        assertFailsWith<IndexOutOfBoundsException> { rope[-1] }
        assertFailsWith<IndexOutOfBoundsException> { rope[3] }
        assertFailsWith<IndexOutOfBoundsException> { rope[4] }
    }

    @Test
    fun testGet_exactlyDefaultChunkSize() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE) { it.toByte() }
        val rope = RopeByteString(data)

        assertEquals(0.toByte(), rope[0])

        assertEquals(
            (RopeByteString.DEFAULT_CHUNK_SIZE - 1).toByte(),
            rope[RopeByteString.DEFAULT_CHUNK_SIZE - 1]
        )

        val middleIndex = RopeByteString.DEFAULT_CHUNK_SIZE / 2
        assertEquals(middleIndex.toByte(), rope[middleIndex])
    }

    @Test
    fun testGet_acrossChunks() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val data = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(data)
        val chunkSize = RopeByteString.DEFAULT_CHUNK_SIZE

        assertEquals((chunkSize - 1).toByte(), rope[chunkSize - 1])
        assertEquals(chunkSize.toByte(), rope[chunkSize])
        assertEquals((chunkSize + 50).toByte(), rope[chunkSize + 50])
    }

    @Test
    fun testGet_withCustomChunkSize() {
        val data = ByteArray(100) { it.toByte() }
        val customChunkSize = 30
        val rope = RopeByteString(data = data, chunkSize = customChunkSize)

        assertEquals(0.toByte(), rope[0])
        assertEquals(29.toByte(), rope[29])
        assertEquals(30.toByte(), rope[30])
        assertEquals(99.toByte(), rope[99])
    }

    @Test
    fun testGet_concatenatedStrings() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val concatenated = rope1 + rope2

        assertEquals(1.toByte(), concatenated[0])
        assertEquals(3.toByte(), concatenated[2])
        assertEquals(4.toByte(), concatenated[3])
        assertEquals(6.toByte(), concatenated[5])
    }

    @Test
    fun testGet_afterSubstring() {
        val original = RopeByteString(1, 2, 3, 4, 5)
        val substring = original.substring(1, 4)  // [2, 3, 4]

        assertEquals(2.toByte(), substring[0])
        assertEquals(3.toByte(), substring[1])
        assertEquals(4.toByte(), substring[2])
    }

    @Test
    fun testGet_repeatedAccess() {
        val rope = RopeByteString(1, 2, 3, 4, 5)

        val index = 2
        assertEquals(3.toByte(), rope[index])
        assertEquals(3.toByte(), rope[index])
        assertEquals(3.toByte(), rope[index])
    }

    @Test
    fun testGet_randomAccess() {
        val size = 1000
        val data = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(*data)

        val indices = listOf(500, 100, 800, 250, 999, 0)
        for (index in indices) {
            assertEquals(data[index], rope[index])
        }
    }

    @Test
    fun testGet_maxChunkSize() {
        val size = RopeByteString.MAX_CHUNK_SIZE + 100
        val data = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(data = data, chunkSize = RopeByteString.MAX_CHUNK_SIZE)

        val maxChunkSize = RopeByteString.MAX_CHUNK_SIZE
        assertEquals((maxChunkSize - 1).toByte(), rope[maxChunkSize - 1])
        assertEquals(maxChunkSize.toByte(), rope[maxChunkSize])
    }

    @Test
    fun testGet_complexScenario() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val concatenated = rope1 + rope2
        val substring = concatenated.substring(1, 5)  // [2, 3, 4, 5]

        assertEquals(2.toByte(), substring[0])
        assertEquals(3.toByte(), substring[1])
        assertEquals(4.toByte(), substring[2])
        assertEquals(5.toByte(), substring[3])
    }

    @Test
    fun testGet_alternatingAccess() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE * 2
        val data = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(data)

        for (index1 in 0..<10) {
            val index2 = RopeByteString.DEFAULT_CHUNK_SIZE + index1

            assertEquals(data[index1], rope[index1])
            assertEquals(data[index2], rope[index2])
        }
    }
}