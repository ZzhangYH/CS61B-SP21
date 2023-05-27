package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /* Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();
    }

    @Test
    /* Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {
        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    /* Check if get and getRecursive methods returns correctly. */
    public void getTest() {
        // Tests for null returns.
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        assertNull("Should return null when calling get on an empty deque", lld1.get(0));
        assertNull("Should return null when calling get on an empty deque", lld1.getRecursive(0));

        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        assertNull("Should return null if the index is invalid", lld1.get(-1));
        assertNull("Should return null if the index is invalid", lld1.getRecursive(-1));
        assertNull("Should return null if the index is invalid", lld1.get(3));
        assertNull("Should return null if the index is invalid", lld1.getRecursive(3));
        assertNull("Should return null if the index is invalid", lld1.get(10));
        assertNull("Should return null if the index is invalid", lld1.getRecursive(10));

        // Tests for valid returns.
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld2.addLast(i);
        }
        assertEquals("Should have the same value", 0, lld2.get(0), 0.0);
        assertEquals("Should have the same value", 0, lld2.getRecursive(0), 0.0);
        assertEquals("Should have the same value", 100, lld2.get(100), 0.0);
        assertEquals("Should have the same value", 100, lld2.getRecursive(100), 0.0);
        assertEquals("Should have the same value", 999999, lld2.get(999999), 0.0);
        assertEquals("Should have the same value", 10000, lld2.getRecursive(10000), 0.0);
    }

    @Test
    /* Check the functionalities of equals method. */
    public void equalsTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld2.addLast(1);
        lld2.addLast(2);
        lld2.addLast(3);
        LinkedListDeque<Integer> lld3 = new LinkedListDeque<Integer>();
        lld3.addLast(1);
        lld3.addLast(2);
        lld3.addLast(3);
        lld3.addLast(4);
        LinkedListDeque<Integer> lld4 = lld1;
        LinkedListDeque<Integer> lld5 = lld2;

        assertFalse("Should not equal to null", lld1.equals(null));
        assertFalse("Should not equal to other types", lld1.equals(new ArrayDeque<Integer>()));
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
