package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RopeByteStringStartsWithTest {

    // Tests for startsWith(ByteArray)
    @Test
    fun testStartsWith_byteArray_emptyPrefix() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.startsWith(ByteArray(0)))
    }

    @Test
    fun testStartsWith_byteArray_singleByte() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.startsWith(byteArrayOf(1)))
        assertFalse(rope.startsWith(byteArrayOf(2)))
    }

    @Test
    fun testStartsWith_byteArray_multipleBytesExact() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        assertTrue(rope.startsWith(byteArrayOf(1, 2, 3)))
        assertFalse(rope.startsWith(byteArrayOf(1, 2, 4)))
    }

    @Test
    fun testStartsWith_byteArray_entireContent() {
        val bytes = byteArrayOf(1, 2, 3)
        val rope = RopeByteString(*bytes)
        assertTrue(rope.startsWith(bytes))
    }

    @Test
    fun testStartsWith_byteArray_longerThanContent() {
        val rope = RopeByteString(1, 2, 3)
        assertFalse(rope.startsWith(byteArrayOf(1, 2, 3, 4)))
    }

    @Test
    fun testStartsWith_byteArray_largeRope() {
        // Create rope larger than chunk size
        val largeData = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(*largeData)
        assertTrue(rope.startsWith(largeData.copyOf(500)))
        assertFalse(rope.startsWith(ByteArray(500) { (it + 1).toByte() }))
    }

    @Test
    fun testStartsWith_byteArray_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(*data)
        // Test prefix that spans across chunks
        val prefix = data.copyOfRange(
            RopeByteString.DEFAULT_CHUNK_SIZE - 5,
            RopeByteString.DEFAULT_CHUNK_SIZE + 5
        )
        assertTrue(rope.startsWith(data.copyOf(prefix.size)))
    }

    // Tests for startsWith(RopeByteString)
    @Test
    fun testStartsWith_ropeByteString_empty() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.startsWith(RopeByteString()))
    }

    @Test
    fun testStartsWith_ropeByteString_singleByte() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.startsWith(RopeByteString(1)))
        assertFalse(rope.startsWith(RopeByteString(2)))
    }

    @Test
    fun testStartsWith_ropeByteString_multipleBytesExact() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        assertTrue(rope.startsWith(RopeByteString(1, 2, 3)))
        assertFalse(rope.startsWith(RopeByteString(1, 2, 4)))
    }

    @Test
    fun testStartsWith_ropeByteString_entireContent() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.startsWith(rope))
    }

    @Test
    fun testStartsWith_ropeByteString_longerThanContent() {
        val rope = RopeByteString(1, 2, 3)
        assertFalse(rope.startsWith(RopeByteString(1, 2, 3, 4)))
    }

    @Test
    fun testStartsWith_ropeByteString_largeRope() {
        val largeData = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(*largeData)
        val prefix = RopeByteString(*largeData.copyOf(500))
        assertTrue(rope.startsWith(prefix))

        val differentPrefix = RopeByteString(*ByteArray(500) { (it + 1).toByte() })
        assertFalse(rope.startsWith(differentPrefix))
    }

    @Test
    fun testStartsWith_ropeByteString_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(*data)

        // Create prefix that spans across chunks
        val prefixData = data.copyOfRange(
            RopeByteString.DEFAULT_CHUNK_SIZE - 5,
            RopeByteString.DEFAULT_CHUNK_SIZE + 5
        )
        assertTrue(rope.startsWith(RopeByteString(*data.copyOf(prefixData.size))))
    }

    @Test
    fun testStartsWith_ropeByteString_differentChunkSizes() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val rope = RopeByteString(data = data, chunkSize = 2)
        val prefix = RopeByteString(data = data.copyOf(3), chunkSize = 3)
        assertTrue(rope.startsWith(prefix))
    }

    @Test
    fun testStartsWith_ropeByteString_withSubstring() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val prefix = rope.substring(0, 3)
        assertTrue(rope.startsWith(prefix))
    }
}