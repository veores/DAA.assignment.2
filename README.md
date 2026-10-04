# DAA Assignment 2 - Data Structures

Author: Bekarys Zharylgassyn

Three data structures written from scratch in Java with `int` storage and no `java.util` collections inside them:

| Structure | Description |
|---|---|
| `DynamicArray` | Resizable `int[]`, capacity doubles when full |
| `MyLinkedList` | Doubly linked list with head and tail, nodes store a primitive `int` |
| `MinHeap` | Binary min-heap in an `int[]`, with `insert`, `peek`, `extractMin` and Floyd's `buildHeap` |

`DynamicArray` and `MyLinkedList` implement the same `IntList` interface, so one benchmark drives both. Every structure counts comparisons, moves and steps inside its own operations through `metrics.Metrics`.

## Layout

```
src/main/java/metrics      Metrics (comparisons, moves, steps)
src/main/java/structures   IntList, DynamicArray, MyLinkedList, MinHeap
src/main/java/benchmark    BenchmarkRunner, Scenarios, MemoryBenchmark (JOL)
src/test/java              JUnit 5 tests
scripts/plot.py            builds PNG charts from the CSV files
results/results.csv        all workloads, produced by BenchmarkRunner
results/memory.csv         JOL measurements, produced by MemoryBenchmark
results/plots/             PNG charts
REPORT.md                  complexity table, proofs, plots, discussion
```

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Python 3 with `matplotlib` (only for the plots)

## Build and test (one command)

```
mvn clean test
```

## Run the benchmark

```
mvn -q compile exec:java
```

This writes `results/results.csv`. The default seed is 42. A different seed and output path can be passed:

```
mvn -q compile exec:java -Dexec.args="42 results/results.csv"
```

The same seed gives the same inputs and the same counter values. Times depend on the machine.

## Run the memory benchmark (JOL)

```
mvn -q compile exec:java -Dexec.mainClass=benchmark.MemoryBenchmark
```

This writes `results/memory.csv`. A message that JOL cannot get Instrumentation is harmless; object sizes are still measured.

## Build the plots

```
python3 scripts/plot.py
```

PNG files appear in `results/plots/`.

## Methodology

- Warm-up: 3 untimed runs of every scenario, then 5 timed runs; the median time is reported (min and max are kept too).
- `System.gc()` is called before each timed run. Inputs are generated before the timer starts.
- Timing covers only the operation under test. Building the structure for W2 and W4b happens before the timer and the counters are reset after it.

## Counters

| Counter | Meaning |
|---|---|
| comparisons | one comparison between two `int` keys |
| moves | one write of an element value or of a link field (array shift or copy, node link rewrite, head or tail update, heap slot write) |
| steps | one element or node visited, created or copied (shift iteration, node hop, node creation, resize copy, sift level) |

`toArray()`, `isValidHeap()` and `capacity()` are inspection helpers for tests and are not counted.

## Workloads

| Workload | What is timed | Sizes n |
|---|---|---|
| W1_append | n times `addLast` on an empty list | 1e3, 1e4, 1e5, 1e6 |
| W2_random_get | 1000 random `get(i)` on a list of size n | 1e3, 1e4, 1e5 |
| W3a_insert_front | n times `addFirst` | 1e3 .. 2e4 |
| W3b_insert_random | n times `insert(randomIndex, x)` | 1e3 .. 2e4 |
| W4a_heap_insert | n times `MinHeap.insert` of random values | 1e3 .. 1e6 |
| W4b_heap_extract_all | n times `extractMin` | 1e3 .. 1e6 |
| W5a_build_by_insert | build a heap with n separate `insert` calls | 1e3 .. 1e6 |
| W5b_build_floyd | build a heap with `buildHeap(int[])` | 1e3 .. 1e6 |

## CSV columns

`workload, structure, n, operations, seed, warmup_runs, measured_runs, median_time_ms, min_time_ms, max_time_ms, comparisons, moves, steps`

Counter columns belong to the run whose time is the median.

## Git

Development used feature branches merged into `main`. Release tag: `v1.0`.
