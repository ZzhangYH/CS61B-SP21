package deque;

public interface Deque<T> {

    /** Adds an item of type T to the front of the deque. */
    void addFirst(T item);

    /** Adds an item of type T to the back of the deque. */
    void addLast(T item);

    /** Returns true if deque is empty, false otherwise. */
    default boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the number of items in the deque. */
    int size();

    /** Prints the items in the deque from first to last. */
    void printDeque();

    /** Removes and returns the item at the front of the deque. */
    T removeFirst();

    /** Removes and returns the item at the back of the deque. */
    T removeLast();

    /** Gets the item at the given index, using iteration. */
    T get(int index);

}
