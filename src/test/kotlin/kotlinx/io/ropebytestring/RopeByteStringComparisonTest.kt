package kotlinx.io.ropebytestring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RopeByteStringComparisonTest {

    @Test
    fun testEquals_identicalObjects() {
        val rope = RopeByteString(1, 2, 3)
        assertEquals(rope, rope)
    }

    @Test
    fun testEquals_identicalContent() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3)
        assertEquals(rope1, rope2)
        assertEquals(rope2, rope1)
    }

    @Test
    fun testEquals_differentContent() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 4)
        assertNotEquals(rope1, rope2)
        assertNotEquals(rope2, rope1)
    }

    @Test
    fun testEquals_differentLengths() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3, 4)
        assertNotEquals(rope1, rope2)
        assertNotEquals(rope2, rope1)
    }

    @Test
    fun testEquals_emptyStrings() {
        val rope1 = RopeByteString()
        val rope2 = RopeByteString()
        assertEquals(rope1, rope2)
    }

    @Test
    fun testEquals_withNull() {
        val rope = RopeByteString(1, 2, 3)
        val ropeNull: RopeByteString? = null
        assertNotEquals(rope, ropeNull)
    }

    @Test
    fun testEquals_differentChunkSizes() {
        val data = byteArrayOf(1, 2, 3, 4, 5)
        val rope1 = RopeByteString(data = data, chunkSize = 2)
        val rope2 = RopeByteString(data = data, chunkSize = 3)
        assertEquals(rope1, rope2)
    }

    @Test
    fun testEquals_largeStrings() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val data = ByteArray(size) { it.toByte() }
        val rope1 = RopeByteString(data)
        val rope2 = RopeByteString(data)
        assertEquals(rope1, rope2)
    }

    @Test
    fun testEquals_concatenatedStrings() {
        val rope1 = RopeByteString(1, 2) + RopeByteString(3, 4)
        val rope2 = RopeByteString(1, 2, 3, 4)
        assertEquals(rope1, rope2)
    }

    @Test
    fun testHashCode_equalObjects() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3)
        assertEquals(rope1.hashCode(), rope2.hashCode())
    }

    @Test
    fun testHashCode_differentObjects() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 4)
        assertNotEquals(rope1.hashCode(), rope2.hashCode())
    }

    @Test
    fun testHashCode_emptyStrings() {
        val rope1 = RopeByteString()
        val rope2 = RopeByteString()
        assertEquals(rope1.hashCode(), rope2.hashCode())
    }

    @Test
    fun testHashCode_consistency() {
        val rope = RopeByteString(1, 2, 3)
        val firstHash = rope.hashCode()
        val secondHash = rope.hashCode()
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun testHashCode_largeStrings() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val data = ByteArray(size) { it.toByte() }
        val rope1 = RopeByteString(data)
        val rope2 = RopeByteString(data)
        assertEquals(rope1.hashCode(), rope2.hashCode())
    }

    @Test
    fun testHashCode_concatenated() {
        val rope1 = RopeByteString(1, 2) + RopeByteString(3, 4)
        val rope2 = RopeByteString(1, 2, 3, 4)
        assertEquals(rope1.hashCode(), rope2.hashCode())
    }

    @Test
    fun testCompareTo_equalStrings() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3)
        assertEquals(0, rope1.compareTo(rope2))
    }

    @Test
    fun testCompareTo_differentLengths() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3, 4)
        assertTrue(rope1 < rope2)
        assertTrue(rope2 > rope1)
    }

    @Test
    fun testCompareTo_differentContent() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 4)
        assertTrue(rope1 < rope2)
        assertTrue(rope2 > rope1)
    }

    @Test
    fun testCompareTo_emptyStrings() {
        val rope1 = RopeByteString()
        val rope2 = RopeByteString()
        assertEquals(0, rope1.compareTo(rope2))
    }

    @Test
    fun testCompareTo_emptyAndNonEmpty() {
        val empty = RopeByteString()
        val nonEmpty = RopeByteString(1)
        assertTrue(empty < nonEmpty)
        assertTrue(nonEmpty > empty)
    }

    @Test
    fun testCompareTo_unsignedComparison() {
        val rope1 = RopeByteString(0xFF.toByte())  // 255 unsigned
        val rope2 = RopeByteString(0x7F.toByte())  // 127 unsigned
        assertTrue(rope1 > rope2)
    }

    @Test
    fun testCompareTo_largeStrings() {
        val size = RopeByteString.DEFAULT_CHUNK_SIZE + 100
        val data1 = ByteArray(size) { it.toByte() }
        val data2 = ByteArray(size) { it.toByte() }
        data2[size - 1] = (data2[size - 1] + 1).toByte()

        val rope1 = RopeByteString(data1)
        val rope2 = RopeByteString(data2)
        assertTrue(rope1 < rope2)
    }

    @Test
    fun testCompareTo_prefixMatch() {
        val rope1 = RopeByteString(1, 2, 3, 4)
        val rope2 = RopeByteString(1, 2, 3, 5)
        assertTrue(rope1 < rope2)
    }

    @Test
    fun testCompareTo_sortOrder() {
        val ropes = listOf(
            RopeByteString(3, 2, 1),
            RopeByteString(1, 2, 3),
            RopeByteString(2, 2, 2),
            RopeByteString(1),
            RopeByteString()
        )

        val sorted = ropes.sorted()
        assertEquals(5, sorted.size)
        for (i in 0 until sorted.size - 1) {
            assertTrue(sorted[i] < sorted[i + 1])
        }
    }

    @Test
    fun testComparisonConsistency() {
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3)
        val rope3 = RopeByteString(1, 2, 3)

        assertTrue(rope1 == rope2 && rope2 == rope3 && rope1 == rope3)
        assertTrue(rope1 == rope2 && rope1.hashCode() == rope2.hashCode())
        assertTrue(rope1 == rope2 && rope1.compareTo(rope2) == 0)
    }

    @Test
    fun testHashMapBehavior() {
        val map = HashMap<RopeByteString, String>()
        val rope1 = RopeByteString(1, 2, 3)
        val rope2 = RopeByteString(1, 2, 3)

        map[rope1] = "value"
        assertEquals("value", map[rope2])
    }
}