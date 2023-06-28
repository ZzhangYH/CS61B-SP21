package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private Node root;

    private class Node {
        private K key;
        private V val;
        private int size;
        private Node left, right;

        public Node(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BSTMap() {}

    /* Removes all of the mappings from this map. */
    public void clear() {
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        Node T = find(root, key);
        return T != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node T = find(root, key);
        if (T == null) {
            return null;
        } else {
            return T.val;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size(root);
    }

    private int size(Node T) {
        if (T == null) {
            return 0;
        } else {
            return T.size;
        }
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V val) {
        root = put(root, key, val);
    }

    private Node put(Node T, K key, V val) {
        if (T == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            T.left = put(T.left, key, val);
        } else if (cmp > 0) {
            T.right = put(T.right, key, val);
        } else {
            T.val = val;
        }
        T.size = size(T.left) + size(T.right) + 1;
        return T;
    }

    /* Prints out BSTMap in order of increasing key. */
    public void printInOrder() {
        throw new UnsupportedOperationException();
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private Node find(Node T, K key) {
        if (T == null) {
            return null;
        }
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            return find(T.left, key);
        } else if (cmp > 0) {
            return find(T.right, key);
        } else {
            return T;
        }
    }

}
