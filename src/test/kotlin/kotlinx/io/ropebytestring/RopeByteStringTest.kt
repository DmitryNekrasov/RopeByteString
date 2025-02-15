package kotlinx.io.ropebytestring

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RopeByteStringTest {
    @Test
    fun testToString_emptyString() {
        val rope = RopeByteString()
        assertEquals("RopeByteString(size=0)", rope.toString())
    }

    @Test
    fun testToString_singleByte() {
        // Test with byte value 0xAB
        val rope = RopeByteString(0xAB.toByte())
        assertEquals("RopeByteString(size=1 hex=ab)", rope.toString())
    }

    @Test
    fun testToString_multipleBytesWithinChunkSize() {
        // Test with bytes [0x01, 0x02, 0x03]
        val rope = RopeByteString(0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        assertEquals("RopeByteString(size=3 hex=010203)", rope.toString())
    }

    @Test
    fun testToString_hexRepresentation() {
        // Test various byte values to ensure correct hex representation
        val bytes = byteArrayOf(
            0x00.toByte(),
            0xFF.toByte(),
            0x7F.toByte(),
            0x80.toByte(),
            0xA5.toByte()
        )
        val rope = RopeByteString(*bytes)
        assertEquals("RopeByteString(size=5 hex=00ff7f80a5)", rope.toString())
    }

    @Test
    fun testToString_largeString() {
        // Create a rope byte string larger than the default chunk size
        val bytes = ByteArray(RopeByteString.DEFAULT_CHUNK_SIZE + 1) { 0x41.toByte() } // All 'A' characters
        val rope = RopeByteString(*bytes)
        val expectedSize = RopeByteString.DEFAULT_CHUNK_SIZE + 1
        val expectedHex = "41".repeat(expectedSize)
        assertEquals("RopeByteString(size=$expectedSize hex=$expectedHex)", rope.toString())
    }

    @Test
    fun testToString_utf8Characters() {
        // Test with UTF-8 encoded characters
        val string = "Hello, 世界"
        val bytes = string.encodeToByteArray()
        val rope = RopeByteString(*bytes)
        // Expected hex representation of "Hello, 世界" in UTF-8
        val expectedHex = "48656c6c6f2c20e4b896e7958c"
        assertEquals("RopeByteString(size=${bytes.size} hex=$expectedHex)", rope.toString())
    }

    @Test
    fun testToString_concatenatedStrings() {
        // Test toString after concatenating two rope byte strings
        val rope1 = RopeByteString(0x01.toByte(), 0x02.toByte())
        val rope2 = RopeByteString(0x03.toByte(), 0x04.toByte())
        val concatenated = rope1 + rope2
        assertEquals("RopeByteString(size=4 hex=01020304)", concatenated.toString())
    }
}