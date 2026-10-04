package structures;

import metrics.Metrics;

public interface IntList {

    void addLast(int value);

    void addFirst(int value);

    void insert(int index, int value);

    int get(int index);

    void set(int index, int value);

    int removeAt(int index);

    int removeFirst();

    int removeLast();

    int indexOf(int value);

    default boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    void clear();

    int[] toArray();

    Metrics metrics();
}
