package kotlinx.io.ropebytestring

internal interface RopeByteStringCache {
    operator fun contains(index: Int): Boolean
    operator fun get(index: Int): Byte
    fun update(data: ByteArray, index: Int, offset: Int)
}

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