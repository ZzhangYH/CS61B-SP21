package deque;

public class LinkedListDeque<T> {

    private static class Node<T> {
        public T item;
        public Node<T> prev;
        public Node<T> next;
        public Node(T item) {
            this.item = item;
            this.prev = null;
            this.next = null;
        }
    }

    private int size;
    private final Node<T> sentinel;

    /** Creates an empty linked list deque. */
    public LinkedListDeque() {
        sentinel = new Node<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.prev = sentinel;
        newNode.next = sentinel.next;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.prev = sentinel.prev;
        newNode.next = sentinel;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /** Return true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Return the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last. */
    public void printDeque() {
        // Print nothing if the deque is empty.
        if (isEmpty()) {
            return;
        }
        // Use temp to traverse the deque.
        StringBuilder printStr = new StringBuilder();
        Node<T> temp = sentinel.next;
        printStr.append(temp.item);
        while (temp.next != sentinel) {
            temp = temp.next;
            printStr.append(" ");
            printStr.append(temp.item);
        }
        System.out.println(printStr);
    }

    /** Removes and returns the item at the front of the deque. */
    public T removeFirst() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Use item to hold the return node.
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.next.prev = sentinel;
        size -= 1;
        return item;
    }

    /** Removes and returns the item at the back of the deque. */
    public T removeLast() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Use item to hold the return node.
        T item = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.prev.next = sentinel;
        size -= 1;
        return item;
    }

    /** Get the item at the given index, using iteration. */
    public T get(int index) {
        // If no such item exists, return null.
        if (index < 0 || index > size - 1) {
            return null;
        }
        // Iterate to the desired node at the given index.
        Node<T> temp = sentinel.next;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.item;
    }

    /** Get the item at the given index, using recursion. */
    public T getRecursive(int index) {
        // If no such item exists, return null.
        if (index < 0 || index > size - 1) {
            return null;
        }
        // Call helper to do the recursion.
        return nodeRecursion(index, sentinel.next);
    }

    /** Helper method for getRecursive, takes an extra Node parameter to perform recursion. */
    private T nodeRecursion(int index, Node<T> node) {
        if (index == 0) {
            // Base case.
            return node.item;
        } else {
            // Where recursion really happens.
            return nodeRecursion(index - 1, node.next);
        }
    }
    
}
