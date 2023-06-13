package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private int size;
    private T[] items;
    private int first;
    private int last;

    /** Creates an empty array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        first = 0;
        last = 0;
        size = 0;
    }

    /** Resizes the array. */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int temp = 0;
        for (int i = 0; i < size; i++) {
            newItems[temp] = items[(first + i) % items.length];
            temp += 1;
        }
        items = newItems;
        first = 0;
        if (isEmpty()) {
            last = 0;
        } else {
            last = temp - 1;
        }
    }

    /** Adds an item of type T to the front of the deque. */
    @Override
    public void addFirst(T item) {
        // Checks if the array needs resizing.
        if (size == items.length) {
            resize(size * 2);
        }
        if (!isEmpty()) {
            first = (first + items.length - 1) % items.length;
        }
        items[first] = item;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    @Override
    public void addLast(T item) {
        // Checks if the array needs resizing.
        if (size == items.length) {
            resize(size * 2);
        }
        if (!isEmpty()) {
            last = (last + 1) % items.length;
        }
        items[last] = item;
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
        printStr.append(items[first]);
        for (int i = 1; i < size; i++) {
            printStr.append(" ");
            printStr.append(items[(first + i) % items.length]);
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
        if (items.length >= 16 && size < items.length * 0.25) {
            resize(items.length / 2);
        }
        T returnItem = items[first];
        items[first] = null;
        if (size > 1) {
            first = (first + 1) % items.length;
        }
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
        if (items.length >= 16 && size < items.length * 0.25) {
            resize(items.length / 2);
        }
        T returnItem = items[last];
        items[last] = null;
        if (size > 1) {
            last = (last + items.length - 1) % items.length;
        }
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
        return items[(first + index) % items.length];
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
