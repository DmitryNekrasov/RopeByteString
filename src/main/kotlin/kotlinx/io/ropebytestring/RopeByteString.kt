package kotlinx.io.ropebytestring

public fun RopeByteString(vararg bytes: Byte): RopeByteString {
    return if (bytes.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(bytes)
}

class RopeByteString private constructor(private val root: TreeNode) : Comparable<RopeByteString> {

    constructor(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) :
            this(data.copyOfRange(startIndex, endIndex), null)

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

    override fun compareTo(other: RopeByteString): Int {
        TODO("comparing a bytestring with another bytestring is not implemented")
    }

    private constructor(data: ByteArray, dummy: Any?) : this(TreeNode.createLeaf(data))

    companion object {
        internal val EMPTY: RopeByteString = RopeByteString(ByteArray(0), null)

        fun wrap(byteArray: ByteArray) = RopeByteString(byteArray, null)
    }
}

public fun ByteArray.toRopeByteString(): RopeByteString {
    TODO("converting a ByteArray to RopeByteString is not implemented")
}

public fun RopeByteString.isEmpty(): Boolean {
    TODO("checking if a bytestring is empty is not implemented")
}

public fun RopeByteString.isNotEmpty(): Boolean {
    TODO("checking if a bytestring is not empty is not implemented")
}