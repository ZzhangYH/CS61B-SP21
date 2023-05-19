package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();

        a.addLast(4);
        a.addLast(5);
        a.addLast(6);

        b.addLast(4);
        b.addLast(5);
        b.addLast(6);

        assertEquals(a.removeLast(), b.removeLast());
        assertEquals(a.removeLast(), b.removeLast());
        assertEquals(a.removeLast(), b.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addLast(randVal);
                b.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = a.size();
                System.out.println("size: " + size);
                assertEquals(a.size(), b.size());
            } else if (operationNumber == 2) {
                // getLast
                if (a.size() > 0 && b.size() > 0) {
                    int last = a.getLast();
                    System.out.println("getLast() = " + last);
                    assertEquals(a.getLast(), b.getLast());
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (a.size() > 0 && b.size() > 0) {
                    int last = a.getLast();
                    System.out.println("removeLast() = " + last);
                    assertEquals(a.removeLast(), b.removeLast());
                }
            }
        }
    }
}
