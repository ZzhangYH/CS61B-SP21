package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Yuhan Zhang
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private int length;
    private int size;
    private final double loadFactor;
    private Collection<Node>[] buckets;

    /* Constructors */

    public MyHashMap() {
        length = 0;
        size = 16;
        loadFactor = 0.75;
        buckets = createTable(size);
    }

    public MyHashMap(int initialSize) {
        length = 0;
        size = initialSize;
        loadFactor = 0.75;
        buckets = createTable(size);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        length = 0;
        size = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /** Removes all of the mappings from this map. */
    public void clear() {
        length = 0;
        size = 16;
        buckets = createTable(size);
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node n = find(key);
        if (n == null) {
            return null;
        } else {
            return n.value;
        }
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return length;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        if ((double) length / size > loadFactor) {
            resize();
        }
        Collection<Node> bucket = getBucket(key);
        if (bucket == null) {
            buckets[getIndex(key)] = createBucket();
        }
        Node n = find(key);
        if (n == null) {
            buckets[getIndex(key)].add(createNode(key, value));
            length += 1;
        } else {
            n.value = value;
        }
    }

    /** Removes the mapping for the specified key from this map if present. */
    public V remove(K key) {
        Collection<Node> bucket = getBucket(key);
        if (bucket == null) {
            return null;
        }
        Node n = find(key);
        if (n == null) {
            return null;
        } else {
            buckets[getIndex(key)].remove(n);
            length -= 1;
            return n.value;
        }
    }

    /** Removes the entry for the specified key only if it is currently mapped to
     *  the specified value. */
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        } else {
            return null;
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        if (length == 0) {
            return null;
        }
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node n : bucket) {
                    keys.add(n.key);
                }
            }
        }
        return keys;
    }

    /** Returns MyHashMap Iterator. */
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /** Returns the index of the key, which is reduced from its hashCode. */
    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), size);
    }

    /** Returns the bucket to which the key belongs. */
    private Collection<Node> getBucket(K key) {
        return buckets[getIndex(key)];
    }

    /** Returns the node of the specified key. */
    private Node find(K key) {
        Collection<Node> bucket = getBucket(key);
        if (bucket != null) {
            for (Node n : bucket) {
                if (key.equals(n.key)) {
                    return n;
                }
            }
        }
        return null;
    }

    /** Resizes the map multiplicatively. */
    private void resize() {
        MyHashMap<K, V> temp = new MyHashMap<>(size * 2);
        for (K key : this) {
            temp.put(key, get(key));
        }
        size *= 2;
        buckets = temp.buckets;
    }

}
