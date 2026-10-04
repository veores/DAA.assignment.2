package structures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MyLinkedListTest extends IntListContractTest {

    @Override
    IntList create() {
        return new MyLinkedList();
    }

    @Test
    void getFromHeadSideCountsHops() {
        MyLinkedList chain = new MyLinkedList();
        for (int i = 0; i < 100; i++) {
            chain.addLast(i);
        }
        chain.metrics().reset();
        chain.get(10);
        assertEquals(11, chain.metrics().steps());
    }

    @Test
    void getFromTailSideCountsHops() {
        MyLinkedList chain = new MyLinkedList();
        for (int i = 0; i < 100; i++) {
            chain.addLast(i);
        }
        chain.metrics().reset();
        chain.get(95);
        assertEquals(5, chain.metrics().steps());
    }

    @Test
    void middleAccessIsTheWorstCase() {
        MyLinkedList chain = new MyLinkedList();
        for (int i = 0; i < 100; i++) {
            chain.addLast(i);
        }
        chain.metrics().reset();
        chain.get(50);
        long middle = chain.metrics().steps();
        chain.metrics().reset();
        chain.get(0);
        assertTrue(middle > chain.metrics().steps());
        assertEquals(1, chain.metrics().steps());
    }

    @Test
    void frontInsertionIsConstantWork() {
        MyLinkedList chain = new MyLinkedList();
        chain.addFirst(1);
        chain.metrics().reset();
        chain.addFirst(2);
        long small = chain.metrics().moves();
        for (int i = 0; i < 1000; i++) {
            chain.addFirst(i);
        }
        chain.metrics().reset();
        chain.addFirst(3);
        assertEquals(small, chain.metrics().moves());
        assertTrue(chain.metrics().moves() > 0);
    }

    @Test
    void headAndTailStayConsistentAfterRemovals() {
        MyLinkedList chain = new MyLinkedList();
        chain.addLast(1);
        chain.addLast(2);
        chain.addLast(3);
        chain.removeAt(1);
        chain.addFirst(0);
        chain.addLast(4);
        assertArrayEquals(new int[] {0, 1, 3, 4}, chain.toArray());
        assertEquals(4, chain.get(3));
        assertEquals(0, chain.get(0));
    }

    @Test
    void largeListSupportsRandomAccess() {
        MyLinkedList chain = new MyLinkedList();
        for (int i = 0; i < 50_000; i++) {
            chain.addLast(i);
        }
        assertEquals(25_000, chain.get(25_000));
        assertEquals(49_999, chain.get(49_999));
    }
}
