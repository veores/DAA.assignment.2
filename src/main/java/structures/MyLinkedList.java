package structures;

import metrics.Metrics;

public final class MyLinkedList implements IntList {

    private static final class Node {
        int value;
        Node prev;
        Node next;

        Node(int value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    @Override
    public void addLast(int value) {
        Node node = new Node(value, tail, null);
        metrics.addMoves(3);
        metrics.step();
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        metrics.move();
        tail = node;
        metrics.move();
        size++;
    }

    @Override
    public void addFirst(int value) {
        Node node = new Node(value, null, head);
        metrics.addMoves(3);
        metrics.step();
        if (head == null) {
            tail = node;
        } else {
            head.prev = node;
        }
        metrics.move();
        head = node;
        metrics.move();
        size++;
    }

    @Override
    public void insert(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        if (index == size) {
            addLast(value);
            return;
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        Node successor = nodeAt(index);
        Node predecessor = successor.prev;
        Node node = new Node(value, predecessor, successor);
        metrics.addMoves(3);
        metrics.step();
        predecessor.next = node;
        successor.prev = node;
        metrics.addMoves(2);
        size++;
    }

    @Override
    public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    @Override
    public void set(int index, int value) {
        checkElementIndex(index);
        nodeAt(index).value = value;
        metrics.move();
    }

    @Override
    public int removeAt(int index) {
        checkElementIndex(index);
        Node node = nodeAt(index);
        unlink(node);
        return node.value;
    }

    @Override
    public int removeFirst() {
        checkElementIndex(0);
        metrics.step();
        Node node = head;
        unlink(node);
        return node.value;
    }

    @Override
    public int removeLast() {
        checkElementIndex(size - 1);
        metrics.step();
        Node node = tail;
        unlink(node);
        return node.value;
    }

    @Override
    public int indexOf(int value) {
        int index = 0;
        for (Node current = head; current != null; current = current.next) {
            metrics.step();
            metrics.compare();
            if (current.value == value) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public int[] toArray() {
        int[] copy = new int[size];
        int i = 0;
        for (Node current = head; current != null; current = current.next) {
            copy[i++] = current.value;
        }
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

    private Node nodeAt(int index) {
        Node current;
        long visited = 1;
        if (index < (size >> 1)) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
                visited++;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
                visited++;
            }
        }
        metrics.addSteps(visited);
        return current;
    }

    private void unlink(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        metrics.move();
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        metrics.move();
        size--;
    }
}
