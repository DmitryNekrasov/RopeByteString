package kotlinx.io.ropebytestring.samples

import kotlinx.io.ropebytestring.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RopeByteStringSamples {

    @Test
    fun createRopeByteStringFromVarargBytes() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        assertEquals(5, ropeString.size)
        assertEquals(1, ropeString[0])
        assertEquals(5, ropeString[4])
    }

    @Test
    fun createRopeByteStringFromByteArrayWithCustomChunkSize() {
        val bytes = ByteArray(2000) { it.toByte() }
        val ropeString = RopeByteString(bytes, chunkSize = 500)
        assertEquals(2000, ropeString.size)
        assertEquals(0, ropeString[0])
        assertEquals((-56).toByte(), ropeString[200])
    }

    @Test
    fun convertByteArrayToRopeByteString() {
        val bytes = byteArrayOf(10, 20, 30)
        val ropeString = bytes.toRopeByteString()
        assertEquals(3, ropeString.size)
        assertEquals(10, ropeString[0])
        assertEquals(30, ropeString[2])
    }

    @Test
    fun concatenateTwoRopeByteStrings() {
        val first = RopeByteString(1, 2, 3)
        val second = RopeByteString(4, 5, 6)
        val result = first + second

        assertEquals(6, result.size)
        assertEquals(1, result[0])
        assertEquals(6, result[5])
    }

    @Test
    fun getSubstringFromRopeByteString() {
        val original = RopeByteString(1, 2, 3, 4, 5)
        val substring = original.substring(1, 4)

        assertEquals(3, substring.size)
        assertEquals(2, substring[0])
        assertEquals(4, substring[2])
    }

    @Test
    fun convertRopeByteStringToByteArray() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        val byteArray = ropeString.toByteArray()

        assertEquals(5, byteArray.size)
        assertEquals(1, byteArray[0])
        assertEquals(5, byteArray[4])
    }

    @Test
    fun checkIfRopeByteStringStartsWithByteArray() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        val prefix = byteArrayOf(1, 2, 3)

        assertTrue(ropeString.startsWith(prefix))
        assertFalse(ropeString.startsWith(byteArrayOf(2, 3)))
    }

    @Test
    fun checkIfRopeByteStringStartsWithAnotherRopeByteString() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        val prefix = RopeByteString(1, 2, 3)

        assertTrue(ropeString.startsWith(prefix))
        assertFalse(ropeString.startsWith(RopeByteString(2, 3)))
    }

    @Test
    fun checkIfRopeByteStringEndsWithByteArray() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        val suffix = byteArrayOf(3, 4, 5)

        assertTrue(ropeString.endsWith(suffix))
        assertFalse(ropeString.endsWith(byteArrayOf(2, 3)))
    }

    @Test
    fun checkIfRopeByteStringEndsWithAnotherRopeByteString() {
        val ropeString = RopeByteString(1, 2, 3, 4, 5)
        val suffix = RopeByteString(3, 4, 5)

        assertTrue(ropeString.endsWith(suffix))
        assertFalse(ropeString.endsWith(RopeByteString(2, 3)))
    }

    @Test
    fun complexScenario() {
        var unbalanced = RopeByteString(1, 2, 3)
        for (i in 0..10) {
            unbalanced += RopeByteString((i * 3 + 4).toByte(), (i * 3 + 5).toByte(), (i * 3 + 6).toByte())
        }

        val largeChunk = ByteArray(5000) { (it % 256).toByte() }
        unbalanced += RopeByteString(largeChunk)

        val lastByteBefore = unbalanced[unbalanced.size - 1]
        val balanced = unbalanced.rebalance()
        val lastByteAfter = balanced[balanced.size - 1]

        assertEquals(unbalanced.size, balanced.size)
        assertEquals(lastByteBefore, lastByteAfter)

        for (i in unbalanced.indices) {
            assertEquals(unbalanced[i], balanced[i], "Bytes differ at index $i")
        }

        val unbalancedSubstring = unbalanced.substring(100, 1000)
        val balancedSubstring = balanced.substring(100, 1000)
        assertEquals(unbalancedSubstring.size, balancedSubstring.size)

        for (i in unbalancedSubstring.indices) {
            assertEquals(unbalancedSubstring[i], balancedSubstring[i],
                "Substring bytes differ at index $i")
        }
    }

    @Test
    fun checkRopeByteStringEmptiness() {
        val empty = RopeByteString()
        val nonEmpty = RopeByteString(1, 2, 3)

        assertTrue(empty.isEmpty())
        assertFalse(nonEmpty.isEmpty())
        assertFalse(empty.isNotEmpty())
        assertTrue(nonEmpty.isNotEmpty())
    }

    @Test
    fun accessRopeByteStringIndices() {
        val ropeString = RopeByteString(1, 2, 3)
        val range = ropeString.indices

        assertEquals(0..2, range)
        assertEquals(3, range.count())
    }

    @Test
    fun compareRopeByteStrings() {
        val first = RopeByteString(1, 2, 3)
        val second = RopeByteString(1, 2, 4)
        val third = RopeByteString(1, 2, 3)

        assertTrue(first < second)
        assertEquals(first, third)
        assertTrue(second > first)
    }

    @Test
    fun getStringRepresentationOfRopeByteString() {
        val ropeString = RopeByteString(0xAB.toByte(), 0xCD.toByte())
        assertEquals("RopeByteString(size=2 hex=abcd)", ropeString.toString())
    }
}