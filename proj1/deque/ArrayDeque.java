package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private int size;
    private T[] items;

    /** Creates an empty array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }

    /** Creates a new array for resizing and copying. */
    private void newItems(int capacity, int srcPos, int destPos, int length) {
        T[] newItems = (T[]) new Object[capacity];
        System.arraycopy(items, srcPos, newItems, destPos, length);
        items = newItems;
    }

    /** Adds an item of type T to the front of the deque. */
    @Override
    public void addFirst(T item) {
        // Checks if the array needs resizing.
        if (size == items.length) {
            newItems(size * 2, 0, 0, size);
        }
        newItems(items.length, 0, 1, size);
        items[0] = item;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    @Override
    public void addLast(T item) {
        // Checks if the array needs resizing.
        if (size == items.length) {
            newItems(size * 2, 0, 0, size);
        }
        items[size] = item;
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
        StringBuilder printStr = new StringBuilder();
        printStr.append(items[0]);
        for (int i = 1; i < size; i++) {
            printStr.append(" ");
            printStr.append(items[i]);
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
        // Halving the size of the array deque when USING_RATIO < 0.25 for memory efficiency.
        if (size >= 4 && size < items.length * 0.25) {
            newItems(items.length / 2, 0, 0, size);
        }
        T returnItem = items[0];
        newItems(items.length, 1, 0, size - 1);
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
        // Halving the size of the array deque when USING_RATIO < 0.25 for memory efficiency.
        if (size >= 4 && size < items.length * 0.25) {
            newItems(items.length / 2, 0, 0, size);
        }
        T returnItem = items[size - 1];
        items[size - 1] = null;
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
        return items[index];
    }

    /** Returns an ArrayDeque iterator. */
    @Override
    public Iterator<T> iterator() {
        return new ADequeItr();
    }

    private class ADequeItr implements Iterator<T> {
        private int cursor;

        ADequeItr() {
            cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public T next() {
            T returnItem = items[cursor];
            cursor += 1;
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
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> that = (ArrayDeque<T>) o;
        // False if size is different.
        if (this.size != that.size) {
            return false;
        }
        // Traverse both deque and check the item of each node.
        for (int i = 0; i < this.size; i++) {
            if (!this.get(i).equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }

}
