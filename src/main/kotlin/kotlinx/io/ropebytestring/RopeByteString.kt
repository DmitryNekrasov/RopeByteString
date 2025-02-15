package kotlinx.io.ropebytestring

class RopeByteString {

    public val size: Int
        get(): Int = TODO("size getter is not implemented")

    public operator fun plus(other: RopeByteString): RopeByteString {
        TODO("concatenation is not implemented")
    }

    public fun substring(startIndex: Int, endIndex: Int = size): RopeByteString {
        TODO("substring extraction is not implemented")
    }

    public operator fun get(index: Int): Byte {
        TODO("extract a byte by an index is not implemented")
    }

    public fun toByteArray(): ByteArray {
        TODO("converting a bytestring to RopeByteArray is not implemented")
    }
}

public fun ByteArray.toRopeByteString(): RopeByteString {
    TODO("converting a ByteArray to RopeByteString is not implemented")
}