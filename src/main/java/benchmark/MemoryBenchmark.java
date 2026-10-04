package benchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import metrics.Metrics;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

public final class MemoryBenchmark {

    private static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000};

    private MemoryBenchmark() {
    }

    public static void main(String[] args) throws IOException {
        Path output = Paths.get(args.length > 0 ? args[0] : "results/memory.csv");
        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(output))) {
            out.println("structure,n,bytes,megabytes,bytes_per_element");
            for (int n : SIZES) {
                IntList array = new DynamicArray();
                IntList list = new MyLinkedList();
                MinHeap heap = new MinHeap();
                for (int i = 0; i < n; i++) {
                    array.addLast(i);
                    list.addLast(i);
                    heap.insert(n - i);
                }
                write(out, "DynamicArray", n, footprint(array, array.metrics()));
                write(out, "MyLinkedList", n, footprint(list, list.metrics()));
                write(out, "MinHeap", n, footprint(heap, heap.metrics()));
            }
        }
        System.out.println("written " + output.toAbsolutePath());
        printLayouts();
    }

    private static long footprint(Object structure, Metrics counters) {
        long total = GraphLayout.parseInstance(structure).totalSize();
        long counterSize = ClassLayout.parseInstance(counters).instanceSize();
        return total - counterSize;
    }

    private static void write(PrintWriter out, String structure, int n, long bytes) {
        out.println(String.format(Locale.ROOT, "%s,%d,%d,%.4f,%.2f",
                structure, n, bytes, bytes / (1024.0 * 1024.0), (double) bytes / n));
    }

    private static void printLayouts() {
        IntList list = new MyLinkedList();
        list.addLast(1);
        System.out.println(GraphLayout.parseInstance(list).toFootprint());
        System.out.println(ClassLayout.parseInstance(new DynamicArray()).toPrintable());
    }
}
