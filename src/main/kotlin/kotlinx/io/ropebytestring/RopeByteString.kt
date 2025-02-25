@file:Suppress("RedundantVisibilityModifier")

package kotlinx.io.ropebytestring

import kotlin.math.min

/**
 * Creates a rope byte string from the specified byte values.
 *
 * @param bytes the bytes to store in the rope byte string.
 *              Can be provided as individual byte values or as a spread array using the spread (*) operator.
 *
 * @return a new rope byte string containing the specified bytes.
 *         If no bytes are provided, returns an empty rope byte string.
 *
 * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.createRopeByteStringFromVarargBytes
 */
public fun RopeByteString(vararg bytes: Byte): RopeByteString {
    return if (bytes.isEmpty()) RopeByteString.EMPTY else RopeByteString.wrap(bytes)
}

/**
 * Creates a rope byte string from a specified range of bytes in the byte array.
 *
 * @param data the source byte array to create the rope byte string from.
 * @param startIndex the start index (inclusive) in the byte array, defaults to 0.
 * @param endIndex the end index (exclusive) in the byte array, defaults to [data.size].
 * @param chunkSize the maximum size of chunks in the resulting rope byte string.
 *                  Must be in range 1..[RopeByteString.MAX_CHUNK_SIZE],
 *                  defaults to [RopeByteString.DEFAULT_CHUNK_SIZE].
 *                  If specified value is outside this range, [RopeByteString.DEFAULT_CHUNK_SIZE] is used.
 * @param maintainBalance whether to maintain balance in the rope tree structure,
 *                        affects performance of operations, defaults to false.
 *
 * @return a new rope byte string containing the bytes from the specified range of the input array.
 *         If the input array is empty, returns an empty rope byte string.
 *
 * @throws IllegalArgumentException if [startIndex] is greater than [endIndex].
 * @throws IndexOutOfBoundsException if [startIndex] is negative or [endIndex] is greater than [data.size].
 *
 * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.createRopeByteStringFromByteArrayWithCustomChunkSize
 */
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

/**
 * An immutable sequence of bytes stored in a rope data structure.
 *
 * A rope byte string organizes bytes in a binary tree structure where leaf nodes contain
 * chunks of the byte sequence and branch nodes combine these chunks. This structure provides
 * efficient operations for large byte sequences by avoiding copying the entire sequence for
 * operations like concatenation and substring extraction.
 *
 * Key features:
 * - Immutable: All operations create new instances
 * - Chunked storage: Bytes are stored in chunks for efficient memory use
 * - Balanced tree: Can maintain balance for consistent performance
 * - Cached access: Recent byte accesses are cached for improved performance
 *
 * The size of chunks can be configured during construction, with a default of [DEFAULT_CHUNK_SIZE]
 * and maximum of [MAX_CHUNK_SIZE] bytes. Balance maintenance can be enabled to ensure consistent
 * performance at the cost of additional operations during modifications.
 *
 * @property root The root node of the rope tree structure.
 * @property maintainBalance Whether to maintain balance in the rope tree structure during operations.
 * @property cache Cache for recently accessed bytes to improve performance.
 *
 * @see TreeNode
 * @see RopeByteStringCache
 */
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
     *
     * @return a new rope byte string containing the concatenated bytes of this and [other]
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.concatenateTwoRopeByteStrings
     */
    public operator fun plus(other: RopeByteString): RopeByteString =
        TreeNode.createBranch(root, other.root).toRopeByteString(maintainBalance || other.maintainBalance)

    /**
     * Returns a new rope byte string starting from [startIndex] and ending at [endIndex].
     *
     * @param startIndex the start index (inclusive) of a subsequence to copy.
     * @param endIndex the end index (exclusive) of a subsequence to copy, [size] be default.
     *
     * @return a new rope byte string containing the bytes from [startIndex] (inclusive) to [endIndex] (exclusive).
     *         Returns an empty rope byte string if [startIndex] equals [endIndex].
     *
     * @throws IllegalArgumentException when `startIndex > endIndex`.
     * @throws IndexOutOfBoundsException when [startIndex] or [endIndex] is out of range of rope byte string indices.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.getSubstringFromRopeByteString
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
     * @return the byte value at the specified [index] in this rope byte string.
     *
     * @throws IndexOutOfBoundsException when [index] is negative or greater or equal to the [size].
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.complexScenario
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
     * @return a new byte array containing the bytes from this rope byte string starting at [startIndex] (inclusive)
     *         through [endIndex] (exclusive). The length of the returned array is `endIndex - startIndex`.
     *
     * @throws IllegalArgumentException when `startIndex > endIndex`.
     * @throws IndexOutOfBoundsException when [startIndex] or [endIndex] is out of range of rope byte string indices.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.convertRopeByteStringToByteArray
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
     *
     * @return `true` if this rope byte string starts with all the bytes in [byteArray] in the same order,
     *         `false` if either the [byteArray] is longer than this rope byte string or if any bytes differ.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.checkIfRopeByteStringStartsWithByteArray
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
     *
     * @return `true` if this rope byte string starts with all the bytes in [ropeByteString] in the same order,
     *         `false` if either the [ropeByteString] is longer than this rope byte string or if any bytes differ.
     *         Returns `true` if both strings are equal.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.checkIfRopeByteStringStartsWithAnotherRopeByteString
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
     *
     * @return `true` if this rope byte string ends with all the bytes in [byteArray] in the same order,
     *         `false` if either the [byteArray] is longer than this rope byte string or if any bytes differ.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.checkIfRopeByteStringEndsWithByteArray
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
     *
     * @return `true` if this rope byte string ends with all the bytes in [ropeByteString] in the same order,
     *         `false` if either the [ropeByteString] is longer than this rope byte string or if any bytes differ.
     *         Returns `true` if both strings are equal.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.checkIfRopeByteStringEndsWithAnotherRopeByteString
     */
    public fun endsWith(ropeByteString: RopeByteString): Boolean = when {
        ropeByteString.size > size -> false
        ropeByteString.size == size -> equals(ropeByteString)
        else -> rangeEquals(size - ropeByteString.size, ropeByteString)
    }

    /**
     * Creates a new balanced rope byte string containing the same sequence of bytes as this rope byte string.
     *
     * The rebalancing process collects all leaf nodes and merges them into a new balanced tree structure.
     * A balanced tree structure ensures more uniform access times across the byte string.
     *
     * @return a new rope byte string with a balanced tree structure containing the same bytes as this rope byte string.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.complexScenario
     */
    public fun rebalance(): RopeByteString = RopeByteString(merge(collectAllLeaves()))

    /**
     * Returns a string representation of this rope byte string. A string representation consists of [size] and
     * a hexadecimal-encoded string of the byte sequence stored in the rope byte string
     *
     * Note that a string representation includes the whole rope byte string content encoded.
     * Due to limitations exposed for the maximum string length, an attempt to return a string representation
     * of too long rope byte string may fail.
     *
     * @return a string in the format `RopeByteString(size=N hex=<hexadecimal>)` where N is the size and
     *         <hexadecimal> is the hex representation of all bytes, or `RopeByteString(size=0)` for empty strings.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.getStringRepresentationOfRopeByteString
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
     * @param other the other object to compare this rope byte string for equality to.
     *
     * @return `true` if [other] is a rope byte string containing exactly the same byte sequence.
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.compareRopeByteStrings
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
     * @return a hash code based on the content of this rope byte string.
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
     *
     * @sample kotlinx.io.ropebytestring.samples.RopeByteStringSamples.compareRopeByteStrings
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

/**
 * Converts this byte array into a rope byte string.
 *
 * The resulting rope byte string will be split into chunks based on the specified [chunkSize].
 * If the byte array is smaller than or equal to the chunk size, it will be stored in a single chunk.
 *
 * @param chunkSize the maximum size of chunks in the resulting rope byte string.
 *                  Must be in range 1..[RopeByteString.MAX_CHUNK_SIZE],
 *                  defaults to [RopeByteString.DEFAULT_CHUNK_SIZE].
 *
 * @return a new rope byte string containing all bytes from this byte array.
 *         If this byte array is empty, returns an empty rope byte string.
 */
public fun ByteArray.toRopeByteString(chunkSize: Int = RopeByteString.DEFAULT_CHUNK_SIZE): RopeByteString =
    RopeByteString(data = this, chunkSize = chunkSize)

/**
 * @return the range of valid byte indices for this rope byte string.
 */
public val RopeByteString.indices: IntRange
    get() = 0..<size

/**
 * @return `true` if this rope byte string is empty.
 */
public fun RopeByteString.isEmpty(): Boolean = size == 0

/**
 * @return `true` if this rope byte string is not empty.
 */
public fun RopeByteString.isNotEmpty(): Boolean = !isEmpty()