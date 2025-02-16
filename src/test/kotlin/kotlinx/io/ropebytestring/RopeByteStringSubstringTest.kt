package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RopeByteStringSubstringTest {

    @Test
    fun testSubstring_entireString() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val substring = rope.substring(0, 5)
        assertEquals(rope.toString(), substring.toString())
    }

    @Test
    fun testSubstring_emptyResult() {
        val rope = RopeByteString(1, 2, 3)
        val substring = rope.substring(1, 1)
        assertTrue(substring.isEmpty())
        assertEquals("RopeByteString(size=0)", substring.toString())
    }

    @Test
    fun testSubstring_singleByte() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val substring = rope.substring(2, 3)
        assertEquals("RopeByteString(size=1 hex=03)", substring.toString())
    }

    @Test
    fun testSubstring_multipleBytesMiddle() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val substring = rope.substring(1, 4)
        assertEquals("RopeByteString(size=3 hex=020304)", substring.toString())
    }

    @Test
    fun testSubstring_startToEnd() {
        val rope = RopeByteString(1, 2, 3, 4, 5)
        val substring = rope.substring(2)
        assertEquals("RopeByteString(size=3 hex=030405)", substring.toString())
    }

    @Test
    fun testSubstring_acrossChunks() {
        val data = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 100) { it.toByte() }
        val rope = RopeByteString(data)

        val start = RopeByteString.DEFAULT_CHUNK_SIZE - 50
        val end = RopeByteString.DEFAULT_CHUNK_SIZE + 50
        val substring = rope.substring(start, end)

        assertEquals(100, substring.size)
        for (i in 0..<100) {
            assertEquals(data[start + i], substring[i])
        }
    }

    @Test
    fun testSubstring_withCustomChunkSize() {
        val data = ByteArray(100) { it.toByte() }
        val rope = RopeByteString(data = data, chunkSize = 20)
        val substring = rope.substring(15, 85)
        assertEquals(70, substring.size)
        for (i in 0..<70) {
            assertEquals(data[i + 15], substring[i])
        }
    }

    @Test
    fun testSubstring_chainedCalls() {
        val rope = RopeByteString(1, 2, 3, 4, 5, 6, 7, 8)
        val substring1 = rope.substring(2, 6)  // [3, 4, 5, 6]
        val substring2 = substring1.substring(1, 3)  // [4, 5]
        assertEquals("RopeByteString(size=2 hex=0405)", substring2.toString())
    }

    @Test
    fun testSubstring_invalidRanges() {
        val rope = RopeByteString(1, 2, 3, 4, 5)

        assertFailsWith<IllegalArgumentException> {
            rope.substring(3, 2)
        }

        assertFailsWith<IllegalArgumentException> {
            rope.substring(-1, 3)
        }

        assertFailsWith<IllegalArgumentException> {
            rope.substring(0, 6)
        }

        assertFailsWith<IllegalArgumentException> {
            rope.substring(6, 7)
        }
    }

    @Test
    fun testSubstring_largeData() {
        val size = RopeByteString.MAX_CHUNK_SIZE * 2
        val data = ByteArray(size) { it.toByte() }
        val rope = RopeByteString(data = data, chunkSize = RopeByteString.MAX_CHUNK_SIZE)

        val start = RopeByteString.MAX_CHUNK_SIZE - 100
        val end = RopeByteString.MAX_CHUNK_SIZE + 100
        val substring = rope.substring(start, end)

        assertEquals(200, substring.size)
        for (i in 0..<200) {
            assertEquals(data[start + i], substring[i])
        }
    }

    @Test
    fun testSubstring_boundaryConditions() {
        val rope = RopeByteString(1, 2, 3, 4, 5)

        val firstByte = rope.substring(0, 1)
        assertEquals("RopeByteString(size=1 hex=01)", firstByte.toString())

        val lastByte = rope.substring(4, 5)
        assertEquals("RopeByteString(size=1 hex=05)", lastByte.toString())

        val emptyStart = rope.substring(0, 0)
        assertTrue(emptyStart.isEmpty())

        val emptyEnd = rope.substring(5, 5)
        assertTrue(emptyEnd.isEmpty())
    }

    @Test
    fun testSubstring_concatenatedRopes() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(4, 5, 6)
        val concatenated = rope1 + rope2

        val sub1 = concatenated.substring(0, 2)
        assertEquals("RopeByteString(size=2 hex=0102)", sub1.toString())

        val sub2 = concatenated.substring(3, 5)
        assertEquals("RopeByteString(size=2 hex=0405)", sub2.toString())

        val sub3 = concatenated.substring(2, 4)
        assertEquals("RopeByteString(size=2 hex=0304)", sub3.toString())
    }
}