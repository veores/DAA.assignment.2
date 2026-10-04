package structures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class IntListContractTest {

    protected IntList list;

    abstract IntList create();

    @BeforeEach
    void setUp() {
        list = create();
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertArrayEquals(new int[0], list.toArray());
    }

    @Test
    void addLastKeepsOrder() {
        for (int i = 0; i < 20; i++) {
            list.addLast(i * 3);
        }
        assertEquals(20, list.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i * 3, list.get(i));
        }
    }

    @Test
    void addFirstReversesOrder() {
        for (int i = 0; i < 10; i++) {
            list.addFirst(i);
        }
        int[] expected = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        assertArrayEquals(expected, list.toArray());
    }

    @Test
    void insertAtBeginningMiddleAndEnd() {
        list.addLast(10);
        list.addLast(30);
        list.insert(1, 20);
        list.insert(0, 5);
        list.insert(4, 40);
        assertArrayEquals(new int[] {5, 10, 20, 30, 40}, list.toArray());
    }

    @Test
    void insertIntoEmptyList() {
        list.insert(0, 7);
        assertArrayEquals(new int[] {7}, list.toArray());
    }

    @Test
    void insertRejectsInvalidIndex() {
        list.addLast(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(2, 5));
        assertEquals(1, list.size());
    }

    @Test
    void getAndSetRejectInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, 1));
        list.addLast(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, 1));
    }

    @Test
    void setReplacesValue() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.set(1, 99);
        assertArrayEquals(new int[] {1, 99, 3}, list.toArray());
    }

    @Test
    void removeAtFromAllPositions() {
        for (int i = 0; i < 6; i++) {
            list.addLast(i);
        }
        assertEquals(0, list.removeAt(0));
        assertEquals(5, list.removeAt(4));
        assertEquals(2, list.removeAt(1));
        assertArrayEquals(new int[] {1, 3, 4}, list.toArray());
    }

    @Test
    void removeFirstAndLast() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        assertEquals(1, list.removeFirst());
        assertEquals(3, list.removeLast());
        assertEquals(2, list.removeFirst());
        assertTrue(list.isEmpty());
    }

    @Test
    void removeFromEmptyListFails() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeFirst());
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeLast());
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(0));
    }

    @Test
    void removingSingleElementLeavesUsableList() {
        list.addLast(4);
        assertEquals(4, list.removeLast());
        assertTrue(list.isEmpty());
        list.addFirst(8);
        list.addLast(9);
        assertArrayEquals(new int[] {8, 9}, list.toArray());
    }

    @Test
    void indexOfAndContains() {
        list.addLast(5);
        list.addLast(6);
        list.addLast(5);
        assertEquals(0, list.indexOf(5));
        assertEquals(1, list.indexOf(6));
        assertEquals(-1, list.indexOf(7));
        assertTrue(list.contains(6));
        assertFalse(list.contains(7));
    }

    @Test
    void extremeValuesAreStored() {
        list.addLast(Integer.MIN_VALUE);
        list.addLast(Integer.MAX_VALUE);
        list.addLast(0);
        assertEquals(Integer.MIN_VALUE, list.get(0));
        assertEquals(Integer.MAX_VALUE, list.get(1));
        assertEquals(2, list.indexOf(0));
    }

    @Test
    void clearEmptiesList() {
        for (int i = 0; i < 50; i++) {
            list.addLast(i);
        }
        list.clear();
        assertTrue(list.isEmpty());
        list.addLast(1);
        assertArrayEquals(new int[] {1}, list.toArray());
    }

    @Test
    void randomOperationsMatchReferenceModel() {
        Random random = new Random(12345);
        List<Integer> model = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int action = random.nextInt(6);
            int value = random.nextInt(1000) - 500;
            if (action == 0 || model.isEmpty()) {
                list.addLast(value);
                model.add(value);
            } else if (action == 1) {
                list.addFirst(value);
                model.add(0, value);
            } else if (action == 2) {
                int index = random.nextInt(model.size() + 1);
                list.insert(index, value);
                model.add(index, value);
            } else if (action == 3) {
                int index = random.nextInt(model.size());
                assertEquals(model.remove(index), list.removeAt(index));
            } else if (action == 4) {
                int index = random.nextInt(model.size());
                list.set(index, value);
                model.set(index, value);
            } else {
                assertEquals(model.indexOf(value), list.indexOf(value));
            }
            assertEquals(model.size(), list.size());
        }
        int[] expected = model.stream().mapToInt(Integer::intValue).toArray();
        assertArrayEquals(expected, list.toArray());
    }

    @Test
    void countersAreNonZeroForEveryMutation() {
        list.addLast(1);
        assertTrue(list.metrics().moves() > 0);
        list.metrics().reset();
        list.addFirst(2);
        assertTrue(list.metrics().moves() > 0);
        assertTrue(list.metrics().steps() > 0);
        list.metrics().reset();
        list.insert(1, 3);
        assertTrue(list.metrics().moves() > 0);
        assertTrue(list.metrics().steps() > 0);
        list.metrics().reset();
        list.removeAt(1);
        assertTrue(list.metrics().moves() > 0);
        list.metrics().reset();
        list.indexOf(99);
        assertEquals(2, list.metrics().comparisons());
        assertEquals(2, list.metrics().steps());
    }

    @Test
    void getCountsStepsAndNoComparisons() {
        for (int i = 0; i < 10; i++) {
            list.addLast(i);
        }
        list.metrics().reset();
        list.get(5);
        assertTrue(list.metrics().steps() >= 1);
        assertEquals(0, list.metrics().comparisons());
        assertEquals(0, list.metrics().moves());
    }
}
