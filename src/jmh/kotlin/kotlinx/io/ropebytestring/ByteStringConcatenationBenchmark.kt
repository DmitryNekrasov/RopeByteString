package kotlinx.io.ropebytestring

import kotlinx.io.bytestring.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@Warmup(iterations = 20, time = 1)
@Measurement(iterations = 10, time = 1)
open class ByteStringConcatenationBenchmark {
    @Param("100", "1000", "10000")
    private var stringSize: Int = 0

    @Param("10", "100", "1000")
    private var listSize: Int = 0

    private lateinit var strings: List<ByteString>

    @Setup
    fun setup() {
        strings = List(listSize) {
            ByteString(ByteArray(stringSize) { Random.nextBytes(1).first() })
        }
    }

    @Benchmark
    fun sequentialConcatenation(blackhole: Blackhole) {
        var result = ByteString()
        for (str in strings) {
            result += str
        }
        blackhole.consume(result)
    }

    @Benchmark
    fun binaryTreeConcatenation(blackhole: Blackhole) {
        fun concatenateRange(start: Int, end: Int): ByteString {
            return when (end - start) {
                0 -> ByteString()
                1 -> strings[start]
                else -> {
                    val mid = (start + end) / 2
                    concatenateRange(start, mid) + concatenateRange(mid, end)
                }
            }
        }

        val result = concatenateRange(0, strings.size)
        blackhole.consume(result)
    }
}

operator fun ByteString.plus(other: ByteString): ByteString {
    if (this.isEmpty()) return other
    if (other.isEmpty()) return this
    val result = ByteArray(this.size + other.size)
    this.copyInto(result, 0)
    other.copyInto(result, this.size)
    return ByteString(result)
}