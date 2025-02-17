package kotlinx.io.ropebytestring

/**
 * Caching interface for rope byte string byte access operations.
 * Provides mechanisms to store and retrieve recently accessed byte chunks
 * to improve read performance.
 */
internal interface RopeByteStringCache {
    operator fun contains(index: Int): Boolean
    operator fun get(index: Int): Byte
    fun update(data: ByteArray, index: Int, offset: Int)
}

/**
 * Implementation of [RopeByteStringCache] that caches the most recently accessed chunk of bytes.
 *
 * This implementation maintains a single contiguous cached region of bytes, storing:
 * - The byte array chunk
 * - The left boundary (inclusive) of the cached region in the rope byte string
 * - The right boundary (exclusive) of the cached region in the rope byte string
 */
internal class LastChunkRopeByteStringCache : RopeByteStringCache {
    private var left = -1
    private var right = -1
    private var chunk: ByteArray? = null

    override operator fun contains(index: Int): Boolean = index in left..<right

    override fun get(index: Int): Byte = chunk!![index - left]

    override fun update(data: ByteArray, index: Int, offset: Int) {
        left = index - offset
        right = left + data.size
        chunk = data
    }
}