package deque;

public interface Deque<T> {

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item);

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item);

    /** Returns true if deque is empty, false otherwise. */
    default boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the number of items in the deque. */
    public int size();

    /** Prints the items in the deque from first to last. */
    public void printDeque();

    /** Removes and returns the item at the front of the deque. */
    public T removeFirst();

    /** Removes and returns the item at the back of the deque. */
    public T removeLast();

    /** Gets the item at the given index, using iteration. */
    public T get(int index);

}
