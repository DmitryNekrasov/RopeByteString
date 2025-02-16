package kotlinx.io.ropebytestring

import org.junit.jupiter.api.Assertions.*

import kotlin.test.*

class RopeByteStringConversionTest {

    @Test
    fun testToByteArray_empty() {
        val rope = RopeByteString()
        val result = rope.toByteArray()
        assertEquals(0, result.size)
    }

    @Test
    fun testToByteArray_singleByte() {
        val rope = RopeByteString(0xFF.toByte())
        val result = rope.toByteArray()
        assertEquals(1, result.size)
        assertEquals(0xFF.toByte(), result[0])
    }

    @Test
    fun testToByteArray_multipleBytesWithinChunk() {
        val original = byteArrayOf(1, 2, 3, 4, 5)
        val rope = RopeByteString(*original)
        val result = rope.toByteArray()

        assertContentEquals(original, result)
    }

    @Test
    fun testToByteArray_exactlyDefaultChunkSize() {
        val original = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE) { it.toByte() }
        val rope = RopeByteString(*original)
        val result = rope.toByteArray()

        assertContentEquals(original, result)
    }

    @Test
    fun testToByteArray_acrossChunks() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val original = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(*original)
        val result = rope.toByteArray()

        assertContentEquals(original, result)
    }

    @Test
    fun testToByteArray_withRange() {
        val original = byteArrayOf(1, 2, 3, 4, 5)
        val rope = RopeByteString(original)

        val result = rope.toByteArray(1, 4)
        assertContentEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun testToByteArray_invalidRanges() {
        val rope = RopeByteString(1, 2, 3)

        assertFailsWith<IllegalArgumentException> {
            rope.toByteArray(2, 1)
        }

        assertFailsWith<IllegalArgumentException> {
            rope.toByteArray(-1, 2)
        }

        assertFailsWith<IllegalArgumentException> {
            rope.toByteArray(0, 4)
        }
    }

    @Test
    fun testToByteArray_concatenated() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val concatenated = rope1 + rope2

        val result = concatenated.toByteArray()
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5, 6), result)
    }

    @Test
    fun testToByteArray_substring() {
        val original = RopeByteString(1, 2, 3, 4, 5)
        val substring = original.substring(1, 4)
        val result = substring.toByteArray()

        assertContentEquals(byteArrayOf(2, 3, 4), result)
    }

    @Test
    fun testToRopeByteString_empty() {
        val array = ByteArray(0)
        val rope = array.toRopeByteString()
        assertTrue(rope.isEmpty())
    }

    @Test
    fun testToRopeByteString_singleByte() {
        val array = byteArrayOf(0xFF.toByte())
        val rope = array.toRopeByteString()

        assertEquals(1, rope.size)
        assertEquals(0xFF.toByte(), rope[0])
    }

    @Test
    fun testToRopeByteString_defaultChunkSize() {
        val array = ByteArray(100) { it.toByte() }
        val rope = array.toRopeByteString()

        assertEquals(100, rope.size)
        assertContentEquals(array, rope.toByteArray())
    }

    @Test
    fun testToRopeByteString_customChunkSize() {
        val array = ByteArray(100) { it.toByte() }
        val rope = array.toRopeByteString(chunkSize = 30)

        assertEquals(100, rope.size)
        assertContentEquals(array, rope.toByteArray())
    }

    @Test
    fun testToRopeByteString_invalidChunkSize() {
        val array = ByteArray(100) { it.toByte() }

        val rope1 = array.toRopeByteString(chunkSize = -1)
        assertEquals(100, rope1.size)
        assertContentEquals(array, rope1.toByteArray())

        val rope2 = array.toRopeByteString(chunkSize = RopeByteString.MAX_CHUNK_SIZE + 1)
        assertEquals(100, rope2.size)
        assertContentEquals(array, rope2.toByteArray())
    }
}