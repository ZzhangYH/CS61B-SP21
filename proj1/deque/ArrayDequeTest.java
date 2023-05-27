package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic array tests. */
public class ArrayDequeTest {

    @Test
    /* Adds a few things to the array, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        assertTrue("A newly initialized ADeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /* Adds an item, then removes an item, and ensures that ad is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeque with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String>  ad1 = new ArrayDeque<String>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertNull("Should return null when removeFirst is called on an empty Deque,", ad1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,", ad1.removeLast());
    }

    @Test
    /* Check if get and getRecursive methods returns correctly. */
    public void getTest() {
        // Tests for null returns.
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        assertNull("Should return null when calling get on an empty deque", ad1.get(0));

        ad1.addFirst(1);
        ad1.addLast(2);
        ad1.addLast(3);
        assertNull("Should return null if the index is invalid", ad1.get(-1));
        assertNull("Should return null if the index is invalid", ad1.get(3));
        assertNull("Should return null if the index is invalid", ad1.get(10));

        // Tests for valid returns.
        ArrayDeque<Integer> ad2 = new ArrayDeque<Integer>();
        for (int i = 0; i < 8; i++) {
            ad2.addLast(i);
        }
        assertEquals("Should have the same value", 0, ad2.get(0), 0.0);
        assertEquals("Should have the same value", 7, ad2.get(7), 0.0);
        assertEquals("Should have the same value", 3, ad2.get(3), 0.0);
    }

    @Test
    /* Check the functionalities of equals method. */
    public void equalsTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        ArrayDeque<Integer> lld2 = new ArrayDeque<Integer>();
        lld2.addLast(1);
        lld2.addLast(2);
        lld2.addLast(3);
        ArrayDeque<Integer> lld3 = new ArrayDeque<Integer>();
        lld3.addLast(1);
        lld3.addLast(2);
        lld3.addLast(3);
        lld3.addLast(4);
        ArrayDeque<Integer> lld4 = lld1;
        ArrayDeque<Integer> lld5 = lld2;

        assertFalse("Should not equal to null", lld1.equals(null));
        assertFalse("Should not equal to other types", lld1.equals(new LinkedListDeque<Integer>()));
        assertTrue("Should be true", lld1.equals(lld2));
        assertTrue("Should be true", lld1.equals(lld4));
        assertTrue("Should be true", lld2.equals(lld4));
        assertTrue("Should be true", lld5.equals(lld1));
        assertFalse("Should be false", lld1.equals(lld3));
        assertFalse("Should be false", lld2.equals(lld3));
        assertFalse("Should be false", lld3.equals(lld4));
        assertFalse("Should be false", lld3.equals(lld5));
    }

}
