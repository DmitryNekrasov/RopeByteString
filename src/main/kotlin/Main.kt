import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.isEmpty
import kotlinx.io.ropebytestring.RopeByteString
import kotlinx.io.ropebytestring.indices
import kotlinx.io.ropebytestring.isEmpty
import kotlinx.io.ropebytestring.toRopeByteString

fun main() {
    run {
        val empty = ByteString()
        val s1 = ByteString(1, 2, 3)
        val s2 = ByteString(4, 5, 6)

        println("empty = ${empty.isEmpty()}")
        println("empty = $empty, hc = ${empty.hashCode()}")
        println("s1 = $s1, hc = ${s1.hashCode()}")
        println("s2 = $s2, hc = ${s2.hashCode()}")

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
        val s1 = RopeByteString(1, 2, 3)
        val s2 = RopeByteString(4, 5, 6)

        println("empty = ${empty.isEmpty()}")
        println("empty = $empty, hc = ${empty.hashCode()}")
        println("s1 = $s1, hc = ${s1.hashCode()}")
        println("s2 = $s2, hc = ${s2.hashCode()}")

        val bytes = RopeByteString(15, 14, 13) + RopeByteString(12, 11) +
                RopeByteString(10, 9, 8, 7) + RopeByteString(6, 5, 4) +
                RopeByteString(3) + RopeByteString(2, 1)
        println(bytes)
        print("[")
        for (i in 0..<(bytes.size - 1)) {
            print("${bytes[i]}, ")
        }
        println("${bytes[bytes.size - 1]}]")
    }

    run {
        val bs = run {
            ByteString(7, 12, 2, 10, 6, 11, 1, 5, 8, 15, 3, 13, 9, 14, 4)
        }

        val rbs = run {
            val c1 = RopeByteString(7, 12)
            val c2 = RopeByteString(2)
            val c3 = RopeByteString(10)
            val c4 = RopeByteString(6, 11)
            val c5 = RopeByteString(1, 5)
            val c6 = RopeByteString(8, 15)
            val c7 = RopeByteString(3, 13)
            val c8 = RopeByteString(9, 14, 4)
            val b1 = c1 + c2
            val b2 = c3 + c4
            val b3 = c5 + c6
            val b4 = c7 + c8
            val a1 = b1 + b2
            val a2 = b3 + b4
            a1 + a2
        }

        println(" bs: $bs")
        println("rbs: $rbs")

        var errorCount = 0
        for (i in rbs.indices) {
            for (j in i..<rbs.size) {
                val sub1 = bs.substring(i, j)
                val sub2 = rbs.substring(i, j)
                if (!sub1.toByteArray().contentEquals(sub2.toByteArray())) {
                    errorCount++
                }
            }
        }
        println("errorCount = $errorCount")
    }

    run {
        val s1 = RopeByteString(1, 2, 3)
        val s2 = RopeByteString(4, 5)
        val s3 = RopeByteString(1, 2)
        val s4 = RopeByteString(3, 4, 5)
        println(s1 + s2 == s3 + s4)
        println(s1 < s2)
        println(s1 + s2 < s4 + s3)
    }

    run {
        val s = RopeByteString(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)
        println(s)
    }

    run {
        val s = byteArrayOf(1, 2, 3, 4, 5, 6, 7).toRopeByteString(2)
        println(s)
    }
}