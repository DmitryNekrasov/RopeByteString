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

    override fun toString(): String {
        TODO("converting a bytestring to a string is not implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("comparing a bytestring with another object is not implemented")
    }

    override fun hashCode(): Int {
        TODO("calculating a hash code for a bytestring is not implemented")
    }
}

public fun ByteArray.toRopeByteString(): RopeByteString {
    TODO("converting a ByteArray to RopeByteString is not implemented")
}