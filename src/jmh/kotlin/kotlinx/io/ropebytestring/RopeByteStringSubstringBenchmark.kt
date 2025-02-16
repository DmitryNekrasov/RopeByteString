package kotlinx.io.ropebytestring

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(
    value = 1, jvmArgsAppend = [
        "-Xms2g",
        "-Xmx2g",
        "-XX:+UseG1GC"
    ]
)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 2, time = 1)
open class RopeByteStringSubstringBenchmark {
    @Param("100", "1000", "10000", "100000")
    private var stringSize: Int = 0

    @Param("10", "100", "1000", "5000")
    private var chunkSize: Int = 0

    @Param("10", "25", "50", "75", "95")
    private var windowPercentage: Int = 0

    private lateinit var rope: RopeByteString
    private var windowSize: Int = 0

    @Setup
    fun setup() {
        rope = RopeByteString(
            data = ByteArray(stringSize) { Random.nextBytes(1).first() },
            chunkSize = chunkSize
        )
        windowSize = stringSize * windowPercentage / 100
    }

    @Benchmark
    @Fork(jvmArgsAppend = ["-XX:+PrintGC"])
    fun slidingWindow(blackhole: Blackhole) {
        for (i in 0..<(rope.size - windowSize)) {
            val sub = rope.substring(i, i + windowSize)
            blackhole.consume(sub)
        }
    }
}