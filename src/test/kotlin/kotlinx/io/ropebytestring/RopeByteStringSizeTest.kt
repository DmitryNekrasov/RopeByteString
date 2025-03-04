package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RopeByteStringSizeTest {

    @Test
    fun testSize_emptyString() {
        val rope = RopeByteString()
        assertEquals(0, rope.size)
    }

    @Test
    fun testSize_singleByte() {
        val rope = RopeByteString(0xFF.toByte())
        assertEquals(1, rope.size)
    }

    @Test
    fun testSize_multipleBytesWithinChunkSize() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5)
        val rope = RopeByteString(*bytes)
        assertEquals(5, rope.size)
    }

    @Test
    fun testSize_exactlyDefaultChunkSize() {
        val bytes = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE) { it.toByte() }
        val rope = RopeByteString(*bytes)
        assertEquals(RopeByteString.DEFAULT_CHUNK_SIZE, rope.size)
    }

    @Test
    fun testSize_largerThanDefaultChunkSize() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val bytes = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(bytes)
        assertEquals(size, rope.size)
    }

    @Test
    fun testSize_afterConcatenation() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val concatenated = rope1 + rope2
        assertEquals(6, concatenated.size)
    }

    @Test
    fun testSize_withCustomChunkSize() {
        val data = ByteArray(1000) { it.toByte() }
        val customChunkSize = 100
        val rope = RopeByteString(data = data, chunkSize = customChunkSize)
        assertEquals(1000, rope.size)
    }

    @Test
    fun testSize_withSubstring() {
        val original = RopeByteString(1, 2, 3, 4, 5)
        val substring = original.substring(1, 4)
        assertEquals(3, substring.size)
    }

    @Test
    fun testSize_withMaxChunkSize() {
        val size = RopeByteString.MAX_CHUNK_SIZE * 2
        val bytes = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(data = bytes, chunkSize = RopeByteString.MAX_CHUNK_SIZE)
        assertEquals(size, rope.size)
    }

    @Test
    fun testSize_afterMultipleConcatenations() {
        val rope1 = RopeByteString(1, 2)
        val rope2 = RopeByteString(3, 4)
        val rope3 = RopeByteString(5, 6)
        val concatenated = rope1 + rope2 + rope3
        assertEquals(6, concatenated.size)
    }

    @Test
    fun testSize_withPartialByteArray() {
        val bytes = ByteArray(100) { it.toByte() }
        val rope = RopeByteString(data = bytes, startIndex = 20, endIndex = 80)
        assertEquals(60, rope.size)
    }

    @Test
    fun testIsEmpty_newInstance() {
        val rope = RopeByteString()
        assertTrue(rope.isEmpty())
        assertFalse(rope.isNotEmpty())
    }

    @Test
    fun testIsEmpty_nonEmptyInstance() {
        val rope = RopeByteString(1)
        assertFalse(rope.isEmpty())
        assertTrue(rope.isNotEmpty())
    }

    @Test
    fun testIsEmpty_afterClear() {
        val rope = RopeByteString().substring(0, 0)
        assertTrue(rope.isEmpty())
        assertFalse(rope.isNotEmpty())
    }

    @Test
    fun testIsEmpty_concatenation() {
        val empty1 = RopeByteString()
        val empty2 = RopeByteString()
        val concatenated = empty1 + empty2

        assertTrue(concatenated.isEmpty())
        assertFalse(concatenated.isNotEmpty())
    }

    @Test
    fun testIsEmpty_emptySubstring() {
        val rope = RopeByteString(1, 2, 3)
        val emptySubstring = rope.substring(1, 1)

        assertTrue(emptySubstring.isEmpty())
        assertFalse(emptySubstring.isNotEmpty())
    }

    @Test
    fun testIsEmpty_consistencyWithSize() {
        val ropes = listOf(
            RopeByteString(),
            RopeByteString(1),
            RopeByteString(1, 2, 3),
            RopeByteString().substring(0, 0),
            RopeByteString(1, 2, 3).substring(1, 1)
        )

        for (rope in ropes) {
            assertEquals(rope.size == 0, rope.isEmpty())
            assertEquals(rope.size != 0, rope.isNotEmpty())
        }
    }
}