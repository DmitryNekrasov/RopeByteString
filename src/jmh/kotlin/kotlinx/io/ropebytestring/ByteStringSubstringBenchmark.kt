package kotlinx.io.ropebytestring

import kotlinx.io.bytestring.ByteString
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(
    value = 2,
    jvmArgsAppend = [
        "-Xms2G",
        "-Xmx2G",
        "-XX:+UseG1GC",
        "-XX:+AlwaysPreTouch",
        "-XX:+PreserveFramePointer"
    ]
)
@Warmup(iterations = 20, time = 1)
@Measurement(iterations = 10, time = 1)
open class ByteStringSubstringBenchmark {
    @Param("100", "1000", "10000", "100000")
    private var stringSize: Int = 0

    @Param("10", "25", "50", "75", "95")
    private var windowPercentage: Int = 0

    private lateinit var str: ByteString
    private var windowSize: Int = 0

    @Setup
    fun setup() {
        str = ByteString(ByteArray(stringSize) { Random.nextBytes(1).first() })
        windowSize = stringSize * windowPercentage / 100
    }


    @Benchmark
    fun slidingWindow(blackhole: Blackhole) {
        for (i in 0..<(str.size - windowSize)) {
            val sub = str.substring(i, i + windowSize)
            blackhole.consume(sub)
        }
    }
}