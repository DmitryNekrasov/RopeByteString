package kotlinx.io.ropebytestring

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RopeByteStringTest {

    @Test
    fun testToString() {
        val expected = "RopeByteString(size=3 hex=010203)"
        val actual = RopeByteString(1, 2, 3).toString()
        assertEquals(expected, actual)
    }
}