package deque;

public class ArrayDeque<T> {

    private int size;
    private T[] items;

    /** Creates an empty array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }

    /** Creates a new array, takes source, destination position and capacity for resizing and copying. */
    private void newItems(int capacity, int srcPos, int destPos, int length) {
        T[] newItems = (T[]) new Object[capacity];
        System.arraycopy(items, srcPos, newItems, destPos, length);
        items = newItems;
    }

    /** Adds an item of type T to the front of the deque. */
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
    public void addLast(T item) {
        // Checks if the array needs resizing.
        if (size == items.length) {
            newItems(size * 2, 0, 0, size);
        }
        items[size] = item;
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
        StringBuilder printStr = new StringBuilder();
        printStr.append(items[0]);
        for (int i = 1; i < size; i++) {
            printStr.append(" ");
            printStr.append(items[i]);
        }
        System.out.println(printStr);
    }

    /** Removes and returns the item at the front of the deque. */
    public T removeFirst() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Halving the size of the array deque when USING_RATIO < 0.25 for memory efficiency.
        if (size > 16 && size < items.length * 0.25) {
            newItems(items.length / 2, 0, 0, size);
        }
        T returnItem = items[0];
        newItems(items.length, 1, 0, size - 1);
        size -= 1;
        return returnItem;
    }

    /** Removes and returns the item at the back of the deque. */
    public T removeLast() {
        // If no such item exists, return null.
        if (isEmpty()) {
            return null;
        }
        // Halving the size of the array deque when USING_RATIO < 0.25 for memory efficiency.
        if (size > 16 && size < items.length * 0.25) {
            newItems(items.length / 2, 0, 0, size);
        }
        T returnItem = items[size - 1];
        items[size - 1] = null;
        size -= 1;
        return returnItem;
    }

    /** Get the item at the given index, using iteration. */
    public T get(int index) {
        // If no such item exists, return null.
        if (index < 0 || index > size - 1) {
            return null;
        }
        return items[index];
    }

}
