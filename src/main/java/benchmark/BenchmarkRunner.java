package benchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import metrics.Metrics;

public final class BenchmarkRunner {

    static final int WARMUP_RUNS = 3;
    static final int MEASURED_RUNS = 5;
    static final long DEFAULT_SEED = 42L;

    private static volatile long sink;

    private BenchmarkRunner() {
    }

    public static void main(String[] args) throws IOException {
        long seed = args.length > 0 ? Long.parseLong(args[0]) : DEFAULT_SEED;
        Path output = Paths.get(args.length > 1 ? args[1] : "results/results.csv");
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<Scenario> scenarios = Scenarios.all(seed);
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(output))) {
            out.println("workload,structure,n,operations,seed,warmup_runs,measured_runs,"
                    + "median_time_ms,min_time_ms,max_time_ms,comparisons,moves,steps");
            for (Scenario scenario : scenarios) {
                String row = measure(scenario, seed);
                out.println(row);
                out.flush();
                System.out.println(row);
            }
        }
        System.out.println("written " + output.toAbsolutePath() + " (sink=" + sink + ")");
    }

    private static String measure(Scenario scenario, long seed) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            scenario.prepare();
            sink += scenario.execute();
        }
        long[] times = new long[MEASURED_RUNS];
        long[] comparisons = new long[MEASURED_RUNS];
        long[] moves = new long[MEASURED_RUNS];
        long[] steps = new long[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            scenario.prepare();
            scenario.metrics().reset();
            System.gc();
            long start = System.nanoTime();
            sink += scenario.execute();
            times[i] = System.nanoTime() - start;
            Metrics m = scenario.metrics();
            comparisons[i] = m.comparisons();
            moves[i] = m.moves();
            steps[i] = m.steps();
        }
        long[] sorted = times.clone();
        Arrays.sort(sorted);
        long median = sorted[MEASURED_RUNS / 2];
        int medianIndex = 0;
        for (int i = 0; i < MEASURED_RUNS; i++) {
            if (times[i] == median) {
                medianIndex = i;
                break;
            }
        }
        return String.format(Locale.ROOT, "%s,%s,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f,%d,%d,%d",
                scenario.workload(), scenario.structure(), scenario.n(), scenario.operations(),
                seed, WARMUP_RUNS, MEASURED_RUNS,
                median / 1e6, sorted[0] / 1e6, sorted[MEASURED_RUNS - 1] / 1e6,
                comparisons[medianIndex], moves[medianIndex], steps[medianIndex]);
    }
}
