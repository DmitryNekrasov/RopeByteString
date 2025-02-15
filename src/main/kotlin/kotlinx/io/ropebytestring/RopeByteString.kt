package kotlinx.io.ropebytestring

public fun RopeByteString(vararg bytes: Byte): RopeByteString {
    return if (bytes.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(bytes)
}

class RopeByteString private constructor(private val root: TreeNode) : Comparable<RopeByteString> {

    constructor(data: ByteArray, startIndex: Int = 0, endIndex: Int = data.size) :
            this(data.copyOfRange(startIndex, endIndex), null)

    public val size: Int
        get(): Int = root.weight

    public operator fun plus(other: RopeByteString): RopeByteString =
        RopeByteString(TreeNode.createBranch(root, other.root))

    public fun substring(startIndex: Int, endIndex: Int = size): RopeByteString {
        TODO("substring extraction is not implemented")
    }

    public operator fun get(index: Int): Byte {
        require(index in 0..<size) { "index ($index) is out of rope byte string bounds: [0..$size)" }
        return getByteAt(index)
    }

    public fun toByteArray(): ByteArray {
        TODO("converting a bytestring to RopeByteArray is not implemented")
    }

    override fun toString(): String {
        if (isEmpty()) {
            return "RopeByteString(size=0)"
        }
        // format: "RopeByteString(size=XXX hex=YYYY)"
        val sizeStr = size.toString()
        val len = 26 + sizeStr.length + size * 2
        return with(StringBuilder(len)) {
            append("RopeByteString(size=")
            append(sizeStr)
            append(" hex=")
            appendHexRepresentation(root)
            append(')')
        }.toString()
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

    private fun StringBuilder.appendHexRepresentation(node: TreeNode) {
        with(node) {
            when (this) {
                is TreeNode.Leaf -> {
                    for (byte in data) {
                        val b = byte.toInt()
                        append(HEX_DIGITS[(b ushr 4) and 0xf])
                        append(HEX_DIGITS[b and 0xf])
                    }
                }

                is TreeNode.Branch -> {
                    appendHexRepresentation(left)
                    appendHexRepresentation(right)
                }
            }
        }
    }

    private fun getByteAt(index: Int): Byte {
        tailrec fun traverseToLeaf(node: TreeNode, idx: Int): Byte = with(node) {
            when (this) {
                is TreeNode.Leaf -> data[idx]
                is TreeNode.Branch -> if (idx < left.weight) traverseToLeaf(left, idx) else
                    traverseToLeaf(right, idx - left.weight)
            }
        }

        return traverseToLeaf(root, index)
    }

    private constructor(data: ByteArray, @Suppress("UNUSED_PARAMETER") dummy: Any?) :
            this(TreeNode.createLeaf(data))

    companion object {
        internal val EMPTY: RopeByteString = RopeByteString(ByteArray(0), null)

        fun wrap(byteArray: ByteArray) = RopeByteString(byteArray, null)

        private const val HEX_DIGITS = "0123456789abcdef"
    }
}

public fun ByteArray.toRopeByteString(): RopeByteString {
    TODO("converting a ByteArray to RopeByteString is not implemented")
}

public fun RopeByteString.isEmpty(): Boolean = size == 0

public fun RopeByteString.isNotEmpty(): Boolean = !isEmpty()