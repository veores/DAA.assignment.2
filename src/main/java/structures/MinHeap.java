package structures;

import java.util.NoSuchElementException;
import metrics.Metrics;

public final class MinHeap {

    private static final int DEFAULT_CAPACITY = 8;
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    private int[] data;
    private int size;
    private final Metrics metrics = new Metrics();

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + initialCapacity);
        }
        data = new int[initialCapacity];
    }

    public static MinHeap from(int[] array) {
        MinHeap heap = new MinHeap(Math.max(DEFAULT_CAPACITY, array.length));
        heap.buildHeap(array);
        return heap;
    }

    public void insert(int value) {
        ensureCapacity(size + 1);
        siftUp(size, value);
        size++;
    }

    public int peek() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
        int min = data[0];
        size--;
        if (size > 0) {
            siftDown(0, data[size], size);
        }
        return min;
    }

    public void buildHeap(int[] array) {
        if (array.length > data.length) {
            data = new int[array.length];
        }
        for (int i = 0; i < array.length; i++) {
            data[i] = array[i];
            metrics.step();
            metrics.move();
        }
        size = array.length;
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i, data[i], size);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            if (data[(i - 1) >>> 1] > data[i]) {
                return false;
            }
        }
        return true;
    }

    public int[] toArray() {
        int[] copy = new int[size];
        System.arraycopy(data, 0, copy, 0, size);
        return copy;
    }

    public Metrics metrics() {
        return metrics;
    }

    private void siftUp(int index, int value) {
        while (index > 0) {
            int parent = (index - 1) >>> 1;
            metrics.step();
            metrics.compare();
            if (data[parent] <= value) {
                break;
            }
            data[index] = data[parent];
            metrics.move();
            index = parent;
        }
        data[index] = value;
        metrics.move();
    }

    private void siftDown(int index, int value, int limit) {
        int half = limit >>> 1;
        while (index < half) {
            int child = 2 * index + 1;
            metrics.step();
            if (child + 1 < limit) {
                metrics.compare();
                if (data[child + 1] < data[child]) {
                    child++;
                }
            }
            metrics.compare();
            if (value <= data[child]) {
                break;
            }
            data[index] = data[child];
            metrics.move();
            index = child;
        }
        data[index] = value;
        metrics.move();
    }

    private void ensureCapacity(int required) {
        if (required <= data.length) {
            return;
        }
        if (required < 0 || required > MAX_CAPACITY) {
            throw new OutOfMemoryError("required capacity too large");
        }
        long grown = (long) data.length * 2;
        while (grown < required) {
            grown *= 2;
        }
        int newCapacity = (int) Math.min(grown, MAX_CAPACITY);
        int[] bigger = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
            metrics.step();
            metrics.move();
        }
        data = bigger;
    }
}
