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
        StringBuilder message = new StringBuilder();

        for (int i = 1; i <= 500; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            int value = StdRandom.uniform(0, 100);

            if (operationNumber == 0) {
                // addFirst
                sad.addFirst(value);
                ads.addFirst(value);
                message.append("addFirst(").append(value).append(")\n");
            } else if (operationNumber == 1) {
                // addLast
                sad.addLast(value);
                ads.addLast(value);
                message.append("addLast(").append(value).append(")\n");
            } else if (operationNumber == 2) {
                // removeFirst
                if (!ads.isEmpty()) {
                    message.append("removeFirst()\n");
                    assertEquals(String.valueOf(message), sad.removeFirst(), ads.removeFirst());
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (!ads.isEmpty()) {
                    message.append("removeLast()\n");
                    assertEquals(String.valueOf(message), sad.removeLast(), ads.removeLast());
                }
            }
        }
    }
}
