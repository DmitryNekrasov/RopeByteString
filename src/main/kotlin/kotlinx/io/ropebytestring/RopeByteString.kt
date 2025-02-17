@file:Suppress("RedundantVisibilityModifier")

package kotlinx.io.ropebytestring

import kotlin.math.min

public fun RopeByteString(vararg bytes: Byte): RopeByteString {
    return if (bytes.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(bytes)
}

public fun RopeByteString(
    data: ByteArray,
    startIndex: Int = 0,
    endIndex: Int = data.size,
    chunkSize: Int = RopeByteString.DEFAULT_CHUNK_SIZE,
    maintainBalance: Boolean = false
): RopeByteString {
    return if (data.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(
        node = RopeByteString.merge(
            RopeByteString.splitIntoChunks(
                data,
                startIndex,
                endIndex,
                if (chunkSize in 1..RopeByteString.MAX_CHUNK_SIZE) chunkSize else RopeByteString.DEFAULT_CHUNK_SIZE
            )
        ),
        maintainBalance = maintainBalance
    )
}

class RopeByteString private constructor(
    private val root: TreeNode,
    private val maintainBalance: Boolean = false,
    private val cache: RopeByteStringCache = LastChunkRopeByteStringCache(),
) : Comparable<RopeByteString> {

    /**
     * Returns size of this RopeByteString.
     */
    public val size: Int
        get(): Int = root.weight

    /**
     * Concatenates this rope byte string with [other] and returns a new rope byte string.
     * The resulting rope byte string will maintain balance if either this or [other] has balance maintenance enabled.
     *
     * @param other the rope byte string to append to this one
     * @return a new rope byte string containing the concatenated bytes of this and [other]
     */
    public operator fun plus(other: RopeByteString): RopeByteString =
        TreeNode.createBranch(root, other.root).toRopeByteString(maintainBalance || other.maintainBalance)

    /**
     * Returns a new rope byte string starting from [startIndex] and ending at [endIndex].
     *
     * @param startIndex the start index (inclusive) of a subsequence to copy.
     * @param endIndex the end index (exclusive) of a subsequence to copy, [size] be default.
     *
     * @throws IllegalArgumentException when `startIndex > endIndex`.
     * @throws IndexOutOfBoundsException when [startIndex] or [endIndex] is out of range of rope byte string indices.
     */
    public fun substring(startIndex: Int, endIndex: Int = size): RopeByteString {
        requireRange(startIndex, endIndex, size)
        return when {
            startIndex == endIndex -> EMPTY
            else -> substring(root, startIndex, endIndex).toRopeByteString(maintainBalance)
        }
    }

    /**
     * Returns a byte at the given index in this rope byte string.
     *
     * @param index the index to retrieve the byte at.
     *
     * @throws IndexOutOfBoundsException when [index] is negative or greater or equal to the [size].
     */
    public operator fun get(index: Int): Byte {
        if (index !in indices) throw IndexOutOfBoundsException(
            "index ($index) is out of rope byte string bounds: [0..$size)"
        )
        return getByteAt(index)
    }

    /**
     * Returns a copy of subsequence starting at [startIndex] and ending at [endIndex] of a byte sequence
     * stored in this rope byte string.
     *
     * @param startIndex the start index (inclusive) of a subsequence to copy, `0` by default.
     * @param endIndex the end index (exclusive) of a subsequence to copy, [size] be default.
     *
     * @throws IllegalArgumentException when `startIndex > endIndex`.
     * @throws IndexOutOfBoundsException when [startIndex] or [endIndex] is out of range of rope byte string indices.
     */
    public fun toByteArray(startIndex: Int = 0, endIndex: Int = size): ByteArray {
        requireRange(startIndex, endIndex, size)
        val result = ByteArray(endIndex - startIndex)
        for (i in startIndex..<endIndex) {
            result[i - startIndex] = getByteAt(i)
        }
        return result
    }

    /**
     * Returns true if this rope byte string starts with the prefix specified by the [byteArray].
     *
     * Behavior of this method is compatible with [CharSequence.startsWith].
     *
     * @param byteArray the prefix to check for.
     */
    public fun startsWith(byteArray: ByteArray): Boolean = when {
        byteArray.size > size -> false
        else -> rangeEquals(0, byteArray)
    }

    /**
     * Returns true if this rope byte string starts with the prefix specified by the [ropeByteString].
     *
     * Behavior of this method is compatible with [CharSequence.startsWith].
     *
     * @param ropeByteString the prefix to check for.
     */
    public fun startsWith(ropeByteString: RopeByteString): Boolean = when {
        ropeByteString.size > size -> false
        ropeByteString.size == size -> equals(ropeByteString)
        else -> rangeEquals(0, ropeByteString)
    }

    /**
     * Returns true if this rope byte string ends with the suffix specified by the [byteArray].
     *
     * Behavior of this method is compatible with [CharSequence.endsWith].
     *
     * @param byteArray the suffix to check for.
     */
    public fun endsWith(byteArray: ByteArray): Boolean = when {
        byteArray.size > size -> false
        else -> rangeEquals(size - byteArray.size, byteArray)
    }

    /**
     * Returns true if this rope byte string ends with the suffix specified by the [ropeByteString].
     *
     * Behavior of this method is compatible with [CharSequence.endsWith].
     *
     * @param ropeByteString the suffix to check for.
     */
    public fun endsWith(ropeByteString: RopeByteString): Boolean = when {
        ropeByteString.size > size -> false
        ropeByteString.size == size -> equals(ropeByteString)
        else -> rangeEquals(size - ropeByteString.size, ropeByteString)
    }

    public fun rebalance(): RopeByteString = RopeByteString(merge(collectAllLeaves()))

    /**
     * Returns a string representation of this rope byte string. A string representation consists of [size] and
     * a hexadecimal-encoded string of the byte sequence stored in the rope byte string
     *
     * The string representation has the following format `RopeByteString(size=3 hex=ABCDEF)`,
     * for empty strings it's always `RopeByteString(size=0)`.
     *
     * Note that a string representation includes the whole rope byte string content encoded.
     * Due to limitations exposed for the maximum string length, an attempt to return a string representation
     * of too long rope byte string may fail.
     */
    override fun toString(): String {
        if (isEmpty()) {
            return "RopeByteString(size=0)"
        }
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

    /**
     * Returns `true` if [other] is a rope byte string containing exactly the same byte sequence.
     *
     * @param other the other object to compare this rope byte string for equality to.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RopeByteString

        if (other.size != size) return false
        if (other.hashCode != 0 && hashCode != 0 && other.hashCode != hashCode) return false

        for (i in indices) {
            if (other.getByteAt(i) != getByteAt(i)) {
                return false
            }
        }

        return true
    }

    /**
     * Returns a hash code based on the content of this rope byte string.
     */
    override fun hashCode(): Int {
        var hc = hashCode
        if (hc == 0) {
            hc = 1
            for (i in indices) {
                hc = 31 * hc + getByteAt(i)
            }
            hashCode = hc
        }
        return hc
    }

    /**
     * Compares a byte sequence of this rope byte string to [other] rope byte string
     * in lexicographical order.
     * Byte values are compared as unsigned integers.
     *
     * The behavior is similar to [String.compareTo].
     *
     * @param other the rope byte string to compare this string to.
     */
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

    private fun substring(node: TreeNode, startIndex: Int, endIndex: Int): TreeNode {
        if (node.weight == endIndex - startIndex) return node
        return with(node) {
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
    }

    private fun rangeEquals(offset: Int, byteArray: ByteArray): Boolean {
        for (i in byteArray.indices) {
            if (getByteAt(i + offset) != byteArray[i]) {
                return false
            }
        }
        return true
    }

    private fun rangeEquals(offset: Int, ropeByteString: RopeByteString): Boolean {
        for (i in ropeByteString.indices) {
            if (getByteAt(i + offset) != ropeByteString.getByteAt(i)) {
                return false
            }
        }
        return true
    }

    private fun collectAllLeaves(): List<TreeNode> {
        val leaves = mutableListOf<TreeNode>()

        fun collectRecursive(node: TreeNode) {
            when (node) {
                is TreeNode.Leaf -> leaves += node
                is TreeNode.Branch -> {
                    collectRecursive(node.left)
                    collectRecursive(node.right)
                }
            }
        }

        collectRecursive(root)

        return leaves
    }

    private constructor(data: ByteArray, @Suppress("UNUSED_PARAMETER") dummy: Any?) :
            this(TreeNode.createLeaf(data))

    companion object {
        internal val EMPTY: RopeByteString = RopeByteString(ByteArray(0), null)

        internal fun wrap(byteArray: ByteArray) =
            if (byteArray.size <= DEFAULT_CHUNK_SIZE) RopeByteString(byteArray, null) else RopeByteString(byteArray)

        internal fun wrap(node: TreeNode, maintainBalance: Boolean) =
            RopeByteString(node, maintainBalance)

        internal const val DEFAULT_CHUNK_SIZE = 1024

        internal const val MAX_CHUNK_SIZE = 8192

        private const val HEX_DIGITS = "0123456789abcdef"

        private fun requireRange(startIndex: Int, endIndex: Int, upperBound: Int) {
            require(startIndex <= endIndex) { "invalid range: $startIndex > $endIndex" }
            if (startIndex < 0) throw IndexOutOfBoundsException(
                "start index cannot be negative: $startIndex"
            )
            if (endIndex > upperBound) throw IndexOutOfBoundsException(
                "end index out of bounds: $endIndex"
            )
        }

        internal fun splitIntoChunks(
            data: ByteArray,
            startIndex: Int,
            endIndex: Int,
            chunkSize: Int
        ): List<TreeNode> {
            requireRange(startIndex, endIndex, data.size)
            return (startIndex..<endIndex step chunkSize).map { i ->
                TreeNode.createLeaf(data.copyOfRange(i, min(endIndex, i + chunkSize)))
            }
        }

        internal fun merge(nodes: List<TreeNode>, start: Int = 0, end: Int = nodes.size): TreeNode {
            val range = end - start
            if (range == 1) return nodes[start]
            val mid = start + range / 2
            return TreeNode.createBranch(merge(nodes, start, mid), merge(nodes, mid, end))
        }

        private fun TreeNode.toRopeByteString(maintainBalance: Boolean): RopeByteString =
            if (!maintainBalance || isBalanced())
                RopeByteString(this, maintainBalance)
            else
                RopeByteString(this, true).rebalance()
    }
}

public fun ByteArray.toRopeByteString(chunkSize: Int = RopeByteString.DEFAULT_CHUNK_SIZE): RopeByteString =
    RopeByteString(data = this, chunkSize = chunkSize)

public val RopeByteString.indices: IntRange
    get() = 0..<size

public fun RopeByteString.isEmpty(): Boolean = size == 0

public fun RopeByteString.isNotEmpty(): Boolean = !isEmpty()