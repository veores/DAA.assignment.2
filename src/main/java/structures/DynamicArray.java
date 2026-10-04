package structures;

import metrics.Metrics;

public final class DynamicArray implements IntList {

    private static final int DEFAULT_CAPACITY = 8;
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    private int[] data;
    private int size;
    private final Metrics metrics = new Metrics();

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + initialCapacity);
        }
        data = new int[initialCapacity];
    }

    @Override
    public void addLast(int value) {
        ensureCapacity(size + 1);
        data[size] = value;
        metrics.move();
        size++;
    }

    @Override
    public void addFirst(int value) {
        insert(0, value);
    }

    @Override
    public void insert(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            metrics.step();
            metrics.move();
        }
        data[index] = value;
        metrics.move();
        size++;
    }

    @Override
    public int get(int index) {
        checkElementIndex(index);
        metrics.step();
        return data[index];
    }

    @Override
    public void set(int index, int value) {
        checkElementIndex(index);
        metrics.step();
        data[index] = value;
        metrics.move();
    }

    @Override
    public int removeAt(int index) {
        checkElementIndex(index);
        int removed = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            metrics.step();
            metrics.move();
        }
        size--;
        return removed;
    }

    @Override
    public int removeFirst() {
        return removeAt(0);
    }

    @Override
    public int removeLast() {
        return removeAt(size - 1);
    }

    @Override
    public int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            metrics.step();
            metrics.compare();
            if (data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public int[] toArray() {
        int[] copy = new int[size];
        System.arraycopy(data, 0, copy, 0, size);
        return copy;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
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
