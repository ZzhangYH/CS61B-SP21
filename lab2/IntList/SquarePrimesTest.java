package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    /** Test all prime numbers. */
    @Test
    public void testSquarePrimes1() {
        IntList lst = IntList.of(2, 3, 5, 7);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 9 -> 25 -> 49", lst.toString());
        assertTrue(changed);
    }

    /** Test all composite numbers. */
    @Test
    public void testSquarePrimes2() {
        IntList lst = IntList.of(8, 9, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("8 -> 9 -> 10", lst.toString());
        assertFalse(changed);
    }

    /** Test the combination of prime and composite numbers. */
    @Test
    public void testSquarePrimes3() {
        IntList lst = IntList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("0 -> 1 -> 4 -> 9 -> 4 -> 25 -> 6 -> 49 -> 8 -> 9", lst.toString());
        assertTrue(changed);
    }
}
