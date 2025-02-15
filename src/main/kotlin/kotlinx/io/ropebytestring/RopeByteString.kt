package kotlinx.io.ropebytestring

import kotlin.math.min

public fun RopeByteString(vararg bytes: Byte): RopeByteString {
    return if (bytes.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(bytes)
}

class RopeByteString private constructor(
    private val root: TreeNode,
    private val cache: RopeByteStringCache = LastChunkRopeByteStringCache()
) : Comparable<RopeByteString> {

    constructor(
        data: ByteArray,
        startIndex: Int = 0,
        endIndex: Int = data.size,
        maxChunkSize: Int = DEFAULT_MAX_CHUNK_SIZE
    ) : this(merge(splitIntoChunks(data, startIndex, endIndex, maxChunkSize)))

    public val size: Int
        get(): Int = root.weight

    public operator fun plus(other: RopeByteString): RopeByteString =
        RopeByteString(TreeNode.createBranch(root, other.root))

    public fun substring(startIndex: Int, endIndex: Int = size): RopeByteString {
        require(startIndex <= endIndex) { "invalid range: $startIndex > $endIndex" }
        require(startIndex >= 0) { "start index cannot be negative: $startIndex" }
        require(endIndex <= size) { "end index out of bounds: $endIndex" }

        return when {
            startIndex == endIndex -> EMPTY
            else -> RopeByteString(substring(root, startIndex, endIndex))
        }
    }

    public operator fun get(index: Int): Byte {
        require(index in 0..<size) { "index ($index) is out of rope byte string bounds: [0..$size)" }
        return getByteAt(index)
    }

    public fun toByteArray(): ByteArray {
        val result = ByteArray(size)
        for (i in 0..<size) {
            result[i] = getByteAt(i)
        }
        return result
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
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RopeByteString

        if (other.size != size) return false
        if (other.hashCode != 0 && hashCode != 0 && other.hashCode != hashCode) return false

        for (i in 0..<size) {
            if (other.getByteAt(i) != getByteAt(i)) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hc = hashCode
        if (hc == 0) {
            hc = 1
            for (i in 0..<size) {
                hc = 31 * hc + getByteAt(i)
            }
            hashCode = hc
        }
        return hc
    }

    override fun compareTo(other: RopeByteString): Int {
        if (other === this) return 0
        for (i in 0..<min(size, other.size)) {
            val cmp = getByteAt(i).toUByte() compareTo other.getByteAt(i).toUByte()
            if (cmp != 0) return cmp
        }
        return size.compareTo(other.size)
    }

    private var hashCode: Int = 0

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
        if (index in cache) return cache[index]

        fun traverseToLeaf(node: TreeNode, idx: Int): Byte = with(node) {
            when (this) {
                is TreeNode.Leaf -> {
                    cache.update(data, index, idx)
                    data[idx]
                }

                is TreeNode.Branch -> if (idx < left.weight) traverseToLeaf(left, idx) else
                    traverseToLeaf(right, idx - left.weight)
            }
        }

        return traverseToLeaf(root, index)
    }

    private fun substring(node: TreeNode, startIndex: Int, endIndex: Int): TreeNode =
        with(node) {
            when (this) {
                is TreeNode.Leaf -> TreeNode.createLeaf(data.copyOfRange(startIndex, endIndex))
                is TreeNode.Branch -> when {
                    endIndex <= left.weight -> substring(left, startIndex, endIndex)
                    startIndex >= left.weight -> substring(right, startIndex - left.weight, endIndex - left.weight)
                    else -> TreeNode.createBranch(
                        substring(left, startIndex, left.weight),
                        substring(right, 0, endIndex - left.weight)
                    )
                }
            }
        }

    private constructor(data: ByteArray, @Suppress("UNUSED_PARAMETER") dummy: Any?) :
            this(TreeNode.createLeaf(data))

    companion object {
        internal val EMPTY: RopeByteString = RopeByteString(ByteArray(0), null)

        internal fun wrap(byteArray: ByteArray) =
            if (byteArray.size <= DEFAULT_MAX_CHUNK_SIZE) RopeByteString(byteArray, null) else RopeByteString(byteArray)

        internal const val DEFAULT_MAX_CHUNK_SIZE = 10

        private const val HEX_DIGITS = "0123456789abcdef"

        private fun splitIntoChunks(
            data: ByteArray,
            startIndex: Int,
            endIndex: Int,
            maxChunkSize: Int
        ): List<TreeNode> {
            return (startIndex..<endIndex step maxChunkSize).map { i ->
                TreeNode.createLeaf(data.copyOfRange(i, min(endIndex, i + maxChunkSize)))
            }
        }

        private fun merge(nodes: List<TreeNode>, start: Int = 0, end: Int = nodes.size): TreeNode {
            val range = end - start
            if (range == 1) return nodes[start]
            val mid = start + range / 2
            return TreeNode.createBranch(merge(nodes, start, mid), merge(nodes, mid, end))
        }
    }
}

public fun ByteArray.toRopeByteString(maxChunkSize: Int = RopeByteString.DEFAULT_MAX_CHUNK_SIZE): RopeByteString =
    RopeByteString(data = this, maxChunkSize = maxChunkSize)

public fun RopeByteString.isEmpty(): Boolean = size == 0

public fun RopeByteString.isNotEmpty(): Boolean = !isEmpty()