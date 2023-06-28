package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        private K key;
        private V val;
        private Node left, right;

        private Node(K key, V val) {
            this.key = key;
            this.val = val;
        }

        private V remove(Node T) {
            if (this.left == T) {
                this.left = null;
            } else {
                this.right = null;
            }
            return T.val;
        }

        private V removeConnect(Node T, Node C) {
            if (this.left == T) {
                this.left = C;
            } else {
                this.right = C;
            }
            return T.val;
        }
    }

    private Node root;

    private int size;

    public BSTMap() {
        clear();
    }

    /* Removes all of the mappings from this map. */
    public void clear() {
        root = null;
        size = 0;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        Node T = find(root, key);
        return T != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key. */
    public V get(K key) {
        Node T = find(root, key);
        if (T == null) {
            return null;
        } else {
            return T.val;
        }
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V val) {
        root = put(root, key, val);
        size += 1;
    }

    private Node put(Node T, K key, V val) {
        if (T == null) {
            return new Node(key, val);
        }
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            T.left = put(T.left, key, val);
        } else if (cmp > 0) {
            T.right = put(T.right, key, val);
        } else {
            T.val = val;
        }
        return T;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        // Removing the root node.
        if (root.key == key) {
            V val = root.val;
            if (root.left == null && root.right == null) {
                root = null;
            } else if (root.left == null) {
                root = root.right;
            } else if (root.right == null) {
                root = root.left;
            } else {
                root = hibbardRemove(root);
            }
            size -= 1;
            return val;
        }
        // Removing the non-root node.
        Node P = findParent(key);
        Node T = find(root, key);
        if (P == null || T == null) {
            return null;
        }
        size -= 1;
        if (T.left == null && T.right == null) {
            return P.remove(T);
        } else if (T.left == null) {
            return P.removeConnect(T, T.right);
        } else if (T.right == null) {
            return P.removeConnect(T, T.left);
        } else {
            return P.removeConnect(T, hibbardRemove(T));
        }
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        } else {
            return null;
        }
    }

    /* Hibbard deletion case for the remove method. */
    private Node hibbardRemove(Node T) {
        ArrayList<K> keys = keysInOrder(T);
        int idx = keys.indexOf(T.key) + 1;
        K newRoot = keys.get(idx);
        Node newP = findParent(newRoot);
        Node newT = find(root, newRoot);
        if (newT.left == null && newT.right == null) {
            newP.remove(newT);
        } else if (newT.left == null) {
            newP.removeConnect(newT, newT.right);
        } else if (newT.right == null) {
            newP.removeConnect(newT, newT.left);
        }
        newT.left = T.left;
        newT.right = T.right;
        return newT;
    }

    /* Searches for the specified key underneath the specified node, and
     * returns the Node with the key or null if not found. */
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

    /* Driver method for finding the parent node of the specified key, starting from
     * the root node. */
    private Node findParent(K key) {
        if (root == null) {
            return null;
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            return findParent(root, root.left, key);
        } else if (cmp > 0) {
            return findParent(root, root.right, key);
        } else {
            return root;
        }
    }

    /* Helper method for findParent, keeping track of the parent. */
    private Node findParent(Node P, Node T, K key) {
        if (T == null) {
            return null;
        }
        int cmp = key.compareTo(T.key);
        if (cmp < 0) {
            return findParent(T, T.left, key);
        } else if (cmp > 0) {
            return findParent(T, T.right, key);
        } else {
            return P;
        }
    }

    /* Prints out BSTMap in order of increasing key. */
    public void printInOrder() {
        if (root == null) {
            System.out.println("There is nothing in the BSTMap.");
            return;
        }
        ArrayList<K> keys = keysInOrder(root);
        for (K key : keys) {
            System.out.println(key + ": " + get(key));
        }
    }

    /* Returns an ArrayList of keys in increasing order. */
    private ArrayList<K> keysInOrder(Node T) {
        ArrayList<K> keys = new ArrayList<>();
        if (T.left != null) {
            keys.addAll(keysInOrder(T.left));
        }
        keys.add(T.key);
        if (T.right != null) {
            keys.addAll(keysInOrder(T.right));
        }
        return keys;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        return new HashSet<>(keysInOrder(root));
    }

    /* Return the Iterator for the BSTMap utilizing the set Iterator. */
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
