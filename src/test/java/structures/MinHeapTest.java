package structures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MinHeapTest {

    @Test
    void newHeapIsEmpty() {
        MinHeap heap = new MinHeap();
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void emptyHeapRejectsPeekAndExtract() {
        MinHeap heap = new MinHeap();
        assertThrows(NoSuchElementException.class, heap::peek);
        assertThrows(NoSuchElementException.class, heap::extractMin);
    }

    @Test
    void invalidCapacityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new MinHeap(0));
    }

    @Test
    void singleElement() {
        MinHeap heap = new MinHeap();
        heap.insert(42);
        assertEquals(42, heap.peek());
        assertEquals(42, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void peekReturnsMinimumWithoutRemoving() {
        MinHeap heap = new MinHeap();
        heap.insert(5);
        heap.insert(2);
        heap.insert(8);
        assertEquals(2, heap.peek());
        assertEquals(3, heap.size());
    }

    @Test
    void extractReturnsAscendingOrder() {
        MinHeap heap = new MinHeap();
        int[] input = {9, 4, 7, 1, 8, 2, 6, 3, 5, 0};
        for (int value : input) {
            heap.insert(value);
        }
        for (int expected = 0; expected < 10; expected++) {
            assertEquals(expected, heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void heapPropertyHoldsAfterEveryOperation() {
        MinHeap heap = new MinHeap();
        Random random = new Random(7);
        for (int i = 0; i < 2000; i++) {
            if (heap.isEmpty() || random.nextInt(3) != 0) {
                heap.insert(random.nextInt(200) - 100);
            } else {
                heap.extractMin();
            }
            assertTrue(heap.isValidHeap());
        }
    }

    @Test
    void duplicatesAreKept() {
        MinHeap heap = new MinHeap();
        for (int i = 0; i < 10; i++) {
            heap.insert(3);
        }
        heap.insert(1);
        assertEquals(1, heap.extractMin());
        for (int i = 0; i < 10; i++) {
            assertEquals(3, heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void extremeValues() {
        MinHeap heap = new MinHeap();
        heap.insert(Integer.MAX_VALUE);
        heap.insert(Integer.MIN_VALUE);
        heap.insert(0);
        assertEquals(Integer.MIN_VALUE, heap.extractMin());
        assertEquals(0, heap.extractMin());
        assertEquals(Integer.MAX_VALUE, heap.extractMin());
    }

    @Test
    void ascendingAndDescendingInputs() {
        MinHeap ascending = new MinHeap();
        MinHeap descending = new MinHeap();
        for (int i = 0; i < 500; i++) {
            ascending.insert(i);
            descending.insert(500 - i);
        }
        assertTrue(ascending.isValidHeap());
        assertTrue(descending.isValidHeap());
        assertEquals(0, ascending.peek());
        assertEquals(1, descending.peek());
    }

    @Test
    void growthKeepsAllElements() {
        MinHeap heap = new MinHeap(1);
        for (int i = 1000; i > 0; i--) {
            heap.insert(i);
        }
        assertEquals(1024, heap.capacity());
        assertEquals(1000, heap.size());
        assertEquals(1, heap.peek());
    }

    @Test
    void heapSortMatchesArraysSort() {
        Random random = new Random(99);
        int[] values = new int[3000];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
        MinHeap heap = new MinHeap();
        for (int value : values) {
            heap.insert(value);
        }
        int[] sorted = new int[values.length];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = heap.extractMin();
        }
        int[] expected = values.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, sorted);
    }

    @Test
    void buildHeapOnEmptyArray() {
        MinHeap heap = MinHeap.from(new int[0]);
        assertTrue(heap.isEmpty());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void buildHeapOnSingleElement() {
        MinHeap heap = MinHeap.from(new int[] {7});
        assertEquals(7, heap.peek());
        assertEquals(1, heap.size());
    }

    @Test
    void buildHeapProducesValidHeap() {
        Random random = new Random(5);
        int[] values = new int[1001];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(500);
        }
        MinHeap heap = MinHeap.from(values);
        assertTrue(heap.isValidHeap());
        assertEquals(values.length, heap.size());
        int[] expected = values.clone();
        Arrays.sort(expected);
        for (int value : expected) {
            assertEquals(value, heap.extractMin());
        }
    }

    @Test
    void buildHeapDoesNotModifyInput() {
        int[] values = {5, 4, 3, 2, 1};
        int[] copy = values.clone();
        MinHeap.from(values);
        assertArrayEquals(copy, values);
    }

    @Test
    void buildHeapReplacesPreviousContents() {
        MinHeap heap = new MinHeap();
        heap.insert(100);
        heap.insert(200);
        heap.buildHeap(new int[] {3, 1, 2});
        assertEquals(3, heap.size());
        assertEquals(1, heap.peek());
        assertTrue(heap.isValidHeap());
    }

    @Test
    void buildHeapBeatsRepeatedInsertInComparisons() {
        Random random = new Random(11);
        int n = 50_000;
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt();
        }
        MinHeap floyd = new MinHeap(n);
        floyd.buildHeap(values);
        MinHeap inserted = new MinHeap(n);
        for (int value : values) {
            inserted.insert(value);
        }
        assertTrue(floyd.metrics().comparisons() <= 2L * n);
        assertTrue(floyd.metrics().comparisons() < inserted.metrics().comparisons());
    }

    @Test
    void countersAreRecordedInsideOperations() {
        MinHeap heap = new MinHeap();
        heap.insert(10);
        assertEquals(1, heap.metrics().moves());
        assertEquals(0, heap.metrics().comparisons());
        heap.insert(5);
        assertEquals(1, heap.metrics().comparisons());
        assertTrue(heap.metrics().moves() > 1);
        heap.metrics().reset();
        heap.extractMin();
        assertTrue(heap.metrics().moves() > 0);
    }

    @Test
    void clearEmptiesHeap() {
        MinHeap heap = new MinHeap();
        heap.insert(1);
        heap.insert(2);
        heap.clear();
        assertTrue(heap.isEmpty());
        assertFalse(heap.size() > 0);
        heap.insert(9);
        assertEquals(9, heap.peek());
    }
}
