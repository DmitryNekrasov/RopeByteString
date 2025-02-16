package kotlinx.io.ropebytestring

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.indices
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 1)
open class ByteStringIndexAccessBenchmark {
    @Param("10", "100", "1000", "10000", "100000", "1000000")
    private var stringSize: Int = 0

    private lateinit var str: ByteString
    private lateinit var randomIndices: IntArray

    @Setup
    fun setup() {
        str = ByteString(ByteArray(stringSize) { Random.nextBytes(1).first() })
        randomIndices = IntArray(stringSize) { Random.nextInt(0, stringSize) }
    }

    @Benchmark
    fun sequentialAccess(blackhole: Blackhole) {
        for (i in str.indices) {
            blackhole.consume(str[i])
        }
    }

    @Benchmark
    fun randomAccess(blackhole: Blackhole) {
        for (index in randomIndices) {
            blackhole.consume(str[index])
        }
    }
}