package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RopeByteStringRangeEqualsTest {

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
        val rope = RopeByteString(bytes)
        assertTrue(rope.startsWith(bytes))
    }

    @Test
    fun testStartsWith_byteArray_longerThanContent() {
        val rope = RopeByteString(1, 2, 3)
        assertFalse(rope.startsWith(byteArrayOf(1, 2, 3, 4)))
    }

    @Test
    fun testStartsWith_byteArray_largeRope() {
        val largeData = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(largeData)
        assertTrue(rope.startsWith(largeData.copyOf(500)))
        assertFalse(rope.startsWith(ByteArray(500) { (it + 1).toByte() }))
    }

    @Test
    fun testStartsWith_byteArray_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(data)
        val prefix = data.copyOfRange(
            RopeByteString.DEFAULT_CHUNK_SIZE - 5,
            RopeByteString.DEFAULT_CHUNK_SIZE + 5
        )
        assertTrue(rope.startsWith(data.copyOf(prefix.size)))
    }

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
        val rope = RopeByteString(largeData)
        val prefix = RopeByteString(largeData.copyOf(500))
        assertTrue(rope.startsWith(prefix))

        val differentPrefix = RopeByteString(ByteArray(500) { (it + 1).toByte() })
        assertFalse(rope.startsWith(differentPrefix))
    }

    @Test
    fun testStartsWith_ropeByteString_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(*data)

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

    @Test
    fun testEndsWith_byteArray_emptySuffix() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.endsWith(ByteArray(0)))
    }

    @Test
    fun testEndsWith_byteArray_singleByte() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.endsWith(byteArrayOf(3)))
        assertFalse(rope.endsWith(byteArrayOf(2)))
    }

    @Test
    fun testEndsWith_byteArray_multipleBytes() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        assertTrue(rope.endsWith(byteArrayOf(3, 4, 5)))
        assertFalse(rope.endsWith(byteArrayOf(3, 4, 6)))
    }

    @Test
    fun testEndsWith_byteArray_entireContent() {
        val bytes = byteArrayOf(1, 2, 3)
        val rope = RopeByteString(bytes)
        assertTrue(rope.endsWith(bytes))
    }

    @Test
    fun testEndsWith_byteArray_longerThanContent() {
        val rope = RopeByteString(1, 2, 3)
        assertFalse(rope.endsWith(byteArrayOf(0, 1, 2, 3)))
    }

    @Test
    fun testEndsWith_byteArray_largeRope() {
        val largeData = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(*largeData)

        val suffix = largeData.copyOfRange(largeData.size - 500, largeData.size)
        assertTrue(rope.endsWith(suffix))

        val modifiedSuffix = suffix.clone()
        modifiedSuffix[0] = (modifiedSuffix[0] + 1).toByte()
        assertFalse(rope.endsWith(modifiedSuffix))
    }

    @Test
    fun testEndsWith_byteArray_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(*data)

        val suffix = data.copyOfRange(
            RopeByteString.DEFAULT_CHUNK_SIZE - 5,
            data.size
        )
        assertTrue(rope.endsWith(suffix))
    }

    @Test
    fun testEndsWith_ropeByteString_empty() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.endsWith(RopeByteString()))
    }

    @Test
    fun testEndsWith_ropeByteString_singleByte() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.endsWith(RopeByteString(3)))
        assertFalse(rope.endsWith(RopeByteString(2)))
    }

    @Test
    fun testEndsWith_ropeByteString_multipleBytes() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        assertTrue(rope.endsWith(RopeByteString(3, 4, 5)))
        assertFalse(rope.endsWith(RopeByteString(3, 4, 6)))
    }

    @Test
    fun testEndsWith_ropeByteString_entireContent() {
        val rope = RopeByteString(1, 2, 3)
        assertTrue(rope.endsWith(rope))
    }

    @Test
    fun testEndsWith_ropeByteString_longerThanContent() {
        val rope = RopeByteString(1, 2, 3)
        assertFalse(rope.endsWith(RopeByteString(0, 1, 2, 3)))
    }

    @Test
    fun testEndsWith_ropeByteString_largeRope() {
        val largeData = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(largeData)

        val suffix = RopeByteString(largeData.copyOfRange(largeData.size - 500, largeData.size))
        assertTrue(rope.endsWith(suffix))

        val modifiedData = largeData.copyOfRange(largeData.size - 500, largeData.size)
        modifiedData[0] = (modifiedData[0] + 1).toByte()
        val modifiedSuffix = RopeByteString(*modifiedData)
        assertFalse(rope.endsWith(modifiedSuffix))
    }

    @Test
    fun testEndsWith_ropeByteString_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 10) { it.toByte() }
        val rope = RopeByteString(*data)

        val suffixData = data.copyOfRange(
            RopeByteString.DEFAULT_CHUNK_SIZE - 5,
            data.size
        )
        val suffix = RopeByteString(*suffixData)
        assertTrue(rope.endsWith(suffix))
    }

    @Test
    fun testEndsWith_ropeByteString_differentChunkSizes() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val rope = RopeByteString(data = data, chunkSize = 2)
        val suffix = RopeByteString(data = data.copyOfRange(2, 5), chunkSize = 3)
        assertTrue(rope.endsWith(suffix))
    }

    @Test
    fun testEndsWith_ropeByteString_withSubstring() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val suffix = rope.substring(2, 5)  // [3, 4, 5]
        assertTrue(rope.endsWith(suffix))
    }

    @Test
    fun testEndsWith_ropeByteString_concatenated() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val suffix1 = RopeByteString(3, 4)
        val suffix2 = RopeByteString(5)
        val concatenatedSuffix = suffix1 + suffix2
        assertTrue(rope.endsWith(concatenatedSuffix))
    }
}