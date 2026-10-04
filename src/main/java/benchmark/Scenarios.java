package benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import metrics.Metrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

final class Scenarios {

    static final int RANDOM_ACCESS_QUERIES = 1000;

    private Scenarios() {
    }

    static List<Scenario> all(long seed) {
        List<Scenario> scenarios = new ArrayList<>();
        int[] appendSizes = {1_000, 10_000, 100_000, 1_000_000};
        int[] accessSizes = {1_000, 10_000, 100_000};
        int[] insertSizes = {1_000, 2_000, 5_000, 10_000, 20_000};
        int[] heapSizes = {1_000, 10_000, 100_000, 1_000_000};

        for (int n : appendSizes) {
            scenarios.add(append("DynamicArray", DynamicArray::new, n));
            scenarios.add(append("MyLinkedList", MyLinkedList::new, n));
        }
        for (int n : accessSizes) {
            scenarios.add(randomAccess("DynamicArray", DynamicArray::new, n, seed));
            scenarios.add(randomAccess("MyLinkedList", MyLinkedList::new, n, seed));
        }
        for (int n : insertSizes) {
            scenarios.add(insertFront("DynamicArray", DynamicArray::new, n));
            scenarios.add(insertFront("MyLinkedList", MyLinkedList::new, n));
        }
        for (int n : insertSizes) {
            scenarios.add(insertRandom("DynamicArray", DynamicArray::new, n, seed));
            scenarios.add(insertRandom("MyLinkedList", MyLinkedList::new, n, seed));
        }
        for (int n : heapSizes) {
            scenarios.add(heapInsert(n, seed));
            scenarios.add(heapExtract(n, seed));
        }
        for (int n : heapSizes) {
            scenarios.add(buildByInsert(n, seed));
            scenarios.add(buildFloyd(n, seed));
        }
        return scenarios;
    }

    private static int[] randomValues(int n, long seed, long salt) {
        Random random = new Random(seed * 31 + n * 17L + salt);
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt();
        }
        return values;
    }

    private static Scenario append(String name, Supplier<IntList> factory, int n) {
        return new Scenario("W1_append", name, n, n) {
            private IntList list;

            @Override
            void prepare() {
                list = factory.get();
            }

            @Override
            long execute() {
                for (int i = 0; i < n; i++) {
                    list.addLast(i);
                }
                return list.size();
            }

            @Override
            Metrics metrics() {
                return list.metrics();
            }
        };
    }

    private static Scenario randomAccess(String name, Supplier<IntList> factory, int n, long seed) {
        int[] positions = new int[RANDOM_ACCESS_QUERIES];
        Random random = new Random(seed * 31 + n * 17L + 1);
        for (int i = 0; i < positions.length; i++) {
            positions[i] = random.nextInt(n);
        }
        return new Scenario("W2_random_get", name, n, RANDOM_ACCESS_QUERIES) {
            private IntList list;

            @Override
            void prepare() {
                list = factory.get();
                for (int i = 0; i < n; i++) {
                    list.addLast(i);
                }
                list.metrics().reset();
            }

            @Override
            long execute() {
                long sum = 0;
                for (int position : positions) {
                    sum += list.get(position);
                }
                return sum;
            }

            @Override
            Metrics metrics() {
                return list.metrics();
            }
        };
    }

    private static Scenario insertFront(String name, Supplier<IntList> factory, int n) {
        return new Scenario("W3a_insert_front", name, n, n) {
            private IntList list;

            @Override
            void prepare() {
                list = factory.get();
            }

            @Override
            long execute() {
                for (int i = 0; i < n; i++) {
                    list.addFirst(i);
                }
                return list.size();
            }

            @Override
            Metrics metrics() {
                return list.metrics();
            }
        };
    }

    private static Scenario insertRandom(String name, Supplier<IntList> factory, int n, long seed) {
        int[] positions = new int[n];
        Random random = new Random(seed * 31 + n * 17L + 2);
        for (int i = 0; i < n; i++) {
            positions[i] = random.nextInt(i + 1);
        }
        return new Scenario("W3b_insert_random", name, n, n) {
            private IntList list;

            @Override
            void prepare() {
                list = factory.get();
            }

            @Override
            long execute() {
                for (int i = 0; i < n; i++) {
                    list.insert(positions[i], i);
                }
                return list.size();
            }

            @Override
            Metrics metrics() {
                return list.metrics();
            }
        };
    }

    private static Scenario heapInsert(int n, long seed) {
        int[] values = randomValues(n, seed, 3);
        return new Scenario("W4a_heap_insert", "MinHeap", n, n) {
            private MinHeap heap;

            @Override
            void prepare() {
                heap = new MinHeap();
            }

            @Override
            long execute() {
                for (int value : values) {
                    heap.insert(value);
                }
                return heap.peek();
            }

            @Override
            Metrics metrics() {
                return heap.metrics();
            }
        };
    }

    private static Scenario heapExtract(int n, long seed) {
        int[] values = randomValues(n, seed, 3);
        return new Scenario("W4b_heap_extract_all", "MinHeap", n, n) {
            private MinHeap heap;

            @Override
            void prepare() {
                heap = new MinHeap(n);
                for (int value : values) {
                    heap.insert(value);
                }
                heap.metrics().reset();
            }

            @Override
            long execute() {
                long sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += heap.extractMin();
                }
                return sum;
            }

            @Override
            Metrics metrics() {
                return heap.metrics();
            }
        };
    }

    private static Scenario buildByInsert(int n, long seed) {
        int[] values = randomValues(n, seed, 5);
        return new Scenario("W5a_build_by_insert", "MinHeap", n, n) {
            private MinHeap heap;

            @Override
            void prepare() {
                heap = new MinHeap(n);
            }

            @Override
            long execute() {
                for (int value : values) {
                    heap.insert(value);
                }
                return heap.peek();
            }

            @Override
            Metrics metrics() {
                return heap.metrics();
            }
        };
    }

    private static Scenario buildFloyd(int n, long seed) {
        int[] values = randomValues(n, seed, 5);
        return new Scenario("W5b_build_floyd", "MinHeap", n, n) {
            private MinHeap heap;

            @Override
            void prepare() {
                heap = new MinHeap(n);
            }

            @Override
            long execute() {
                heap.buildHeap(values);
                return heap.peek();
            }

            @Override
            Metrics metrics() {
                return heap.metrics();
            }
        };
    }
}
