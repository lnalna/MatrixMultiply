package solution.testing.matrix;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10)
@Measurement(iterations = 10)
@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(10)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {

    private static final int MATRIX_SIZE = 1000;

    @Param({"3", "4", "10"})
    private int threadNumber;

    private static int[][] matrixA;
    private static int[][] matrixB;

    @Setup
    public void setUp() {
        matrixA = MatrixUtil.create(MATRIX_SIZE);
        matrixB = MatrixUtil.create(MATRIX_SIZE);
    }

    private ExecutorService executor;

    @Benchmark
    public int[][] singleThreadMultiplyOpt0() throws Exception {
        return MatrixUtil.singleThreadMultiply0(matrixA, matrixB);
    }

    @Benchmark
    public int[][] singleThreadMultiplyOpt1() throws Exception {
        return MatrixUtil.singleThreadMultiply1(matrixA, matrixB);
    }

    @Benchmark
    public int[][] singleThreadMultiplyOpt2() throws Exception {
        return MatrixUtil.singleThreadMultiply2(matrixA, matrixB);
    }

    @Benchmark
    public int[][] singleThreadMultiplyOpt3() throws Exception {
        return MatrixUtil.singleThreadMultiply3(matrixA, matrixB);
    }

    @Benchmark
    public int[][] concurrentMultiply0() throws Exception {
        return MatrixUtil.concurrentMultiply0(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiply1() throws Exception {
        return MatrixUtil.concurrentMultiply1(matrixA, matrixB, executor);
    }

    @Setup
    public void setup() {
        executor = Executors.newFixedThreadPool(threadNumber);
    }

    @TearDown
    public void tearDown() {
        executor.shutdown();
    }
}
