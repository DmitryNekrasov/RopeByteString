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
open class RopeByteStringIndexAccessBenchmark {
    @Param("10", "100", "1000", "10000", "100000", "1000000")
    private var stringSize: Int = 0

    @Param("10", "100", "1000", "5000")
    private var chunkSize: Int = 0

    private lateinit var rope: RopeByteString
    private lateinit var randomIndices: IntArray

    @Setup
    fun setup() {
        rope = RopeByteString(
            data = ByteArray(stringSize) { Random.nextBytes(1).first() },
            chunkSize = chunkSize
        )
        randomIndices = IntArray(stringSize) { Random.nextInt(0, stringSize) }
    }

    @Benchmark
    fun sequentialAccess(blackhole: Blackhole) {
        for (i in rope.indices) {
            blackhole.consume(rope[i])
        }
    }

    @Benchmark
    fun randomAccess(blackhole: Blackhole) {
        for (index in randomIndices) {
            blackhole.consume(rope[index])
        }
    }
}