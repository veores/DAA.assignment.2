package structures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DynamicArrayTest extends IntListContractTest {

    @Override
    IntList create() {
        return new DynamicArray();
    }

    @Test
    void capacityDoublesWhenFull() {
        DynamicArray array = new DynamicArray(4);
        for (int i = 0; i < 4; i++) {
            array.addLast(i);
        }
        assertEquals(4, array.capacity());
        array.addLast(4);
        assertEquals(8, array.capacity());
        for (int i = 5; i < 9; i++) {
            array.addLast(i);
        }
        assertEquals(16, array.capacity());
    }

    @Test
    void growthPreservesContents() {
        DynamicArray array = new DynamicArray(1);
        for (int i = 0; i < 1000; i++) {
            array.addLast(i);
        }
        int[] contents = array.toArray();
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, contents[i]);
        }
        assertEquals(1024, array.capacity());
    }

    @Test
    void insertTriggersGrowthAtFullCapacity() {
        DynamicArray array = new DynamicArray(2);
        array.addLast(1);
        array.addLast(3);
        array.insert(1, 2);
        assertEquals(4, array.capacity());
        assertArrayEquals(new int[] {1, 2, 3}, array.toArray());
    }

    @Test
    void invalidCapacityIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray(0));
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray(-5));
    }

    @Test
    void frontInsertionCountsEveryShift() {
        DynamicArray array = new DynamicArray(16);
        for (int i = 0; i < 5; i++) {
            array.addLast(i);
        }
        array.metrics().reset();
        array.insert(0, 100);
        assertEquals(5, array.metrics().steps());
        assertEquals(6, array.metrics().moves());
    }

    @Test
    void appendWithoutGrowthCostsOneMove() {
        DynamicArray array = new DynamicArray(16);
        array.addLast(1);
        assertEquals(1, array.metrics().moves());
        assertEquals(0, array.metrics().steps());
    }

    @Test
    void resizeCountsCopiedElements() {
        DynamicArray array = new DynamicArray(4);
        for (int i = 0; i < 4; i++) {
            array.addLast(i);
        }
        array.metrics().reset();
        array.addLast(4);
        assertEquals(4, array.metrics().steps());
        assertEquals(5, array.metrics().moves());
    }

    @Test
    void totalCopyWorkOfAppendsStaysBelowTwiceN() {
        DynamicArray array = new DynamicArray(1);
        int n = 100_000;
        for (int i = 0; i < n; i++) {
            array.addLast(i);
        }
        assertTrue(array.metrics().steps() < 2L * n);
    }

    @Test
    void removeAtShiftsRemainingElements() {
        DynamicArray array = new DynamicArray(16);
        for (int i = 0; i < 6; i++) {
            array.addLast(i);
        }
        array.metrics().reset();
        array.removeAt(1);
        assertEquals(4, array.metrics().moves());
    }
}
