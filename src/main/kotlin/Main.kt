import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.isEmpty
import kotlinx.io.ropebytestring.RopeByteString

fun main() {
    run {
        val empty = ByteString()
        val s1 = ByteString(1, 2, 3)
        val s2 = ByteString(4, 5, 6)

        println("empty = ${empty.isEmpty()}")
        println("s1 = $s1")
        println("s2 = $s2")

        val bytes = ByteString(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
        println(bytes)
        print("[")
        for (i in 0..<(bytes.size - 1)) {
            print("${bytes[i]}, ")
        }
        println("${bytes[bytes.size - 1]}]")

        val sub = bytes.substring(0)
        println(sub)
    }

    run {
        val empty = RopeByteString()
    }
}