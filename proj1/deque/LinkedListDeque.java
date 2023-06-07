package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private static class Node<T> {
        private final T item;
        private Node<T> prev;
        private Node<T> next;
        Node(T item) {
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
    @Override
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.prev = sentinel;
        newNode.next = sentinel.next;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    @Override
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.prev = sentinel.prev;
        newNode.next = sentinel;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /** Returns the number of items in the deque. */
    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last. */
    @Override
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
    @Override
    public T removeFirst() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Use item to hold the return node.
        T returnItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return returnItem;
    }

    /** Removes and returns the item at the back of the deque. */
    @Override
    public T removeLast() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Use item to hold the return node.
        T returnItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return returnItem;
    }

    /** Gets the item at the given index, using iteration. */
    @Override
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

    /** Gets the item at the given index, using recursion. */
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

    /** Returns a LinkedListDeque iterator. */
    @Override
    public Iterator<T> iterator() {
        return new LLDequeItr();
    }

    private class LLDequeItr implements Iterator<T> {
        private Node<T> cursor;

        LLDequeItr() {
            cursor = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return cursor != sentinel;
        }

        @Override
        public T next() {
            T returnItem = cursor.item;
            cursor = cursor.next;
            return returnItem;
        }
    }

    /** Returns whether or not the parameter o is equal to the deque. */
    @Override
    public boolean equals(Object o) {
        // True if the same reference.
        if (o == this) {
            return true;
        }
        // False if o is null.
        if (o == null) {
            return false;
        }
        // Check for type casting.
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<?> that = (LinkedListDeque<?>) o;
        // False if size is different.
        if (this.size != that.size) {
            return false;
        }
        // Traverse both deque and check each item in the array.
        for (int i = 0; i < this.size; i++) {
            if (this.get(i) != that.get(i)) {
                return false;
            }
        }
        return true;
    }

}
