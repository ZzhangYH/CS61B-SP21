package tester;

import static org.junit.Assert.*;

import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();

        for (int i = 1; i <= 500; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            int value = StdRandom.uniform(0, 100);

            if (operationNumber == 0) {
                // addFirst
                sad.addFirst(value);
                ads.addFirst(value);
            } else if (operationNumber == 1) {
                // addLast
                sad.addLast(value);
                ads.addLast(value);
            } else if (operationNumber == 2) {
                // removeFirst
                if (!ads.isEmpty()) {
                    Integer expected = ads.removeFirst();
                    Integer actual = sad.removeFirst();
                    assertEquals("Test failed!\n\t- failed on operation "
                            + i + " of " + 500
                            + "\n\t- student\tremoveFirst() returned " + actual
                            + "\n\t- reference\tremoveFirst() returned " + expected, expected, actual);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (!ads.isEmpty()) {
                    Integer expected = ads.removeLast();
                    Integer actual = sad.removeLast();
                    assertEquals("Test failed!\n\t- failed on operation "
                            + i + " of " + 500
                            + "\n\t- student\tremoveLast() returned " + actual
                            + "\n\t- reference\tremoveLast() returned " + expected, expected, actual);
                }
            }
        }
    }
}
