package kotlinx.io.ropebytestring

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 1)
open class RopeByteStringConcatenationBenchmark {
    @Param("100", "1000", "10000")
    private var stringSize: Int = 0

    @Param("10", "100", "1000", "5000")
    private var chunkSize: Int = 0

    @Param("10", "100", "1000")
    private var listSize: Int = 0

    @Param("true", "false")
    private var balance: Boolean = false

    private lateinit var ropes: List<RopeByteString>

    @Setup
    fun setup() {
        ropes = List(listSize) {
            RopeByteString(
                data = ByteArray(stringSize) { Random.nextBytes(1).first() },
                chunkSize = chunkSize,
                maintainBalance = balance
            )
        }
    }

    @Benchmark
    fun sequentialConcatenation(blackhole: Blackhole) {
        var result = RopeByteString()
        for (rope in ropes) {
            result += rope
        }
        blackhole.consume(result)
    }

    @Benchmark
    fun binaryTreeConcatenation(blackhole: Blackhole) {
        fun concatenateRange(start: Int, end: Int): RopeByteString {
            return when (end - start) {
                0 -> RopeByteString()
                1 -> ropes[start]
                else -> {
                    val mid = (start + end) / 2
                    concatenateRange(start, mid) + concatenateRange(mid, end)
                }
            }
        }

        val result = concatenateRange(0, ropes.size)
        blackhole.consume(result)
    }
}