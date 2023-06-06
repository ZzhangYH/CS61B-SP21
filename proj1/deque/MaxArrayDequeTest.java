package deque;

import java.util.Comparator;

import org.junit.Test;
import static org.junit.Assert.*;


public class MaxArrayDequeTest {

    private static class IntComparator1 implements Comparator<Integer> {
        @Override
        public int compare(Integer t1, Integer t2) {
            return t1 - t2;
        }
    }

    private static class IntComparator2 implements Comparator<Integer> {
        @Override
        public int compare(Integer t1, Integer t2) {
            return t2 - t1;
        }
    }

    private static class StrComparator implements Comparator<String> {
        @Override
        public int compare(String t1, String t2) {
            if (t1.length() < t2.length()) {
                return -1;
            } else if (t1.length() > t2.length()) {
                return 1;
            } else {
                for (int i = 0; i < t1.length(); i++) {
                    if (t1.charAt(i) < t2.charAt(i)) {
                        return -1;
                    } else if (t1.charAt(i) > t2.charAt(i)) {
                        return 1;
                    }
                }
                return 0;
            }
        }
    }

    @Test
    public void NullTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator1());
        MaxArrayDeque<String> mad2 = new MaxArrayDeque<>(new StrComparator());

        assertNull(mad1.max());
        assertNull(mad1.max(new IntComparator2()));
        assertNull(mad2.max());
    }

    @Test
    public void IntTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator1());
        mad1.addLast(1);
        mad1.addLast(1);
        mad1.addLast(2);
        mad1.addLast(3);
        mad1.addLast(5);
        mad1.addLast(8);

        assertEquals("Should return the biggest value", 8, mad1.max(), 0.0);
        assertEquals("Should return the smallest value", 1, mad1.max(new IntComparator2()), 0.0);

        MaxArrayDeque<Integer> mad2 = new MaxArrayDeque<>(new IntComparator1());
        for (int i = 0; i < 10; i++) {
            mad2.addLast(10);
        }

        assertEquals("Should return the biggest value", 10, mad2.max(), 0.0);
        assertEquals("Should return the smallest value", 10, mad2.max(new IntComparator2()), 0.0);
    }

    @Test
    public void StrTest() {
        MaxArrayDeque<String> mad1 = new MaxArrayDeque<>(new StrComparator());
        mad1.addLast("This");
        mad1.addLast("This is");
        mad1.addLast("This is a");
        mad1.addLast("This is a test");
        assertEquals("Should return the longest string", "This is a test", mad1.max());

        MaxArrayDeque<String> mad2 = new MaxArrayDeque<>(new StrComparator());
        for (int i = 0; i < 10; i++) {
            mad2.addLast("test");
        }
        assertEquals("Should return the longest string", "test", mad2.max());
    }

}
