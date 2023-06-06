package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> comparator;

    /** Creates a MaxArrayDeque with the given Comparator. */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    /** Returns the maximum element in the deque as governed by the previously given Comparator. */
    public T max() {
        return max(comparator);
    }

    /** Returns the maximum element in the deque as governed by the parameter Comparator. */
    public T max(Comparator<T> c) {
        // If the deque is empty, return null.
        if (isEmpty()) {
            return null;
        }
        // Traverse the deque and get the maxItem.
        T maxItem = get(0);
        for (T item : this) {
            if (c.compare(item, maxItem) > 0) {
                maxItem = item;
            }
        }
        return maxItem;
    }

}
