package kotlinx.io.ropebytestring

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RopeByteStringToStringTest {
    @Test
    fun testToString_emptyString() {
        val rope = RopeByteString()
        assertEquals("RopeByteString(size=0)", rope.toString())
    }

    @Test
    fun testToString_singleByte() {
        val rope = RopeByteString(0xAB.toByte())
        assertEquals("RopeByteString(size=1 hex=ab)", rope.toString())
    }

    @Test
    fun testToString_multipleBytesWithinChunkSize() {
        val rope = RopeByteString(1, 2, 3)
        assertEquals("RopeByteString(size=3 hex=010203)", rope.toString())
    }

    @Test
    fun testToString_hexRepresentation() {
        val bytes = byteArrayOf(
            0x00.toByte(),
            0xFF.toByte(),
            0x7F.toByte(),
            0x80.toByte(),
            0xA5.toByte()
        )
        val rope = RopeByteString(bytes)
        assertEquals("RopeByteString(size=5 hex=00ff7f80a5)", rope.toString())
    }

    @Test
    fun testToString_largeString() {
        val expectedSize = RopeByteString.DEFAULT_CHUNK_SIZE + 10
        val bytes = ByteArray(expectedSize) { 0x41.toByte() }
        val rope = RopeByteString(bytes)
        val expectedHex = "41".repeat(expectedSize)
        assertEquals("RopeByteString(size=$expectedSize hex=$expectedHex)", rope.toString())
    }

    @Test
    fun testToString_utf8Characters() {
        val string = "Hello, 世界"
        val bytes = string.encodeToByteArray()
        val rope = RopeByteString(bytes)
        val expectedHex = "48656c6c6f2c20e4b896e7958c"
        assertEquals("RopeByteString(size=${bytes.size} hex=$expectedHex)", rope.toString())
    }

    @Test
    fun testToString_concatenatedStrings() {
        val rope1 = RopeByteString(0x01.toByte(), 0x02.toByte())
        val rope2 = RopeByteString(0x03.toByte(), 0x04.toByte())
        val concatenated = rope1 + rope2
        assertEquals("RopeByteString(size=4 hex=01020304)", concatenated.toString())
    }
}