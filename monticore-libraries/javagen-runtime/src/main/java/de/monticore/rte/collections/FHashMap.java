/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import de.monticore.rte.actions.Action2;

import java.lang.ref.SoftReference;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of {@link FMap} using {@link Object#hashCode()} to store elements efficiently.
 * <p>
 * Internally the elements are stored using an AVL tree. Elements with the same hash (Hash collisions) are stored in an array in the tree's nodes.
 * <p>
 * Assuming a good hash function, all common operations run in {@code O(log n)} time ({@link #get(Object)}, {@link #with(Object, Object)},
 * {@link #without(Object)}).
 * <p>
 * There are no guarantees regarding the iteration order.
 * <p>
 * This permits null keys and values.
 *
 * @param <K> The type of the keys in this map
 * @param <V> The type of the values in this map
 */
public class FHashMap<K, V> implements FMap<K, V> {

  static final FHashMap<?, ?> EMPTY = new FHashMap<>();

  protected final Node<K, V> root;
  protected final int size;

  protected EntrySet entrySet;
  protected KeySet keySet;
  protected ValueCollection valueCollection;

  protected FHashMap(Node<K, V> root, int size) {
    this.root = root;
    this.size = size;
  }

  public FHashMap() {
    this(null, 0);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return root == null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean containsKey(Object key) {
    BooleanWrapper wasKeyPresent = new BooleanWrapper();
    Node.get((K) key, Objects.hashCode(key), wasKeyPresent, this.root);
    return wasKeyPresent.value;
  }

  @Override
  public boolean containsValue(Object value) {
    return Node.containsValue(value, this.root);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(Object key) {
    return Node.get((K) key, Objects.hashCode(key), new BooleanWrapper(), this.root);
  }

  @Override
  public FMap<K, V> with(K key, V value) {
    BooleanWrapper wasKeyPresent = new BooleanWrapper();
    return new FHashMap<>(
        Node.withInserted(key, Objects.hashCode(key), value, wasKeyPresent, root),
        size + (wasKeyPresent.value ? 0 : 1)
    );
  }

  @Override
  public FMap<K, V> withAll(FMap<K, V> other) {
    Node<K, V> root = this.root;
    int size = this.size;
    BooleanWrapper wasKeyPresent = new BooleanWrapper();
    for (Entry<K, V> entry : other.entrySet()) {
      root = Node.withInserted(entry.getKey(), Objects.hashCode(entry.getKey()), entry.getValue(), wasKeyPresent, root);
      if (!wasKeyPresent.value) {
        size++;
      }
    }
    return new FHashMap<>(root, size);
  }

  @Override
  public FMap<K, V> without(Object key) {
    Node<K, V> root = Node.withRemoved(key, Objects.hashCode(key), this.root);
    if (root == this.root) {
      return this;
    }
    if (root == null) {
      return new FHashMap<>();
    }
    return new FHashMap<>(root, this.size - 1);
  }

  @Override
  public FMap<K, V> withoutAll(FCollection<?> keys) {
    Node<K, V> root = this.root;
    int size = this.size;
    for (Object key : keys) {
      Node<K, V> newRoot = Node.withRemoved(key, Objects.hashCode(key), root);
      if (newRoot != root) {
        size--;
      }
      root = newRoot;
    }
    if (root == this.root) {
      return this;
    }
    return new FHashMap<>(root, size);
  }

  @Override
  public void forEach(Action2<? super K, ? super V> action) {
    Node.iterateAll(FHashMap.this.root, action);
  }

  @Override
  public FSet<K> keySet() {
    if (this.keySet == null) {
      this.keySet = new KeySet();
    }
    return this.keySet;
  }

  @Override
  public FCollection<V> values() {
    if (this.valueCollection == null) {
      this.valueCollection = new ValueCollection();
    }
    return this.valueCollection;
  }

  @Override
  public FSet<Entry<K, V>> entrySet() {
    if (this.entrySet == null) {
      this.entrySet = new EntrySet();
    }
    return this.entrySet;
  }

  @Override
  public Map<K, V> toJava() {
    if (this.isEmpty()) {
      return Map.of();
    }
    HashMap<K, V> javaMap = new HashMap<>();
    this.forEach(javaMap::put);
    return Collections.unmodifiableMap(javaMap);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FMap<?, ?>)) {
      return false;
    }
    FMap<?, ?> otherMap = (FMap<?, ?>) obj;
    return entrySet().equals(otherMap.entrySet());
  }

  @Override
  public int hashCode() {
    return entrySet().hashCode();
  }

  @Override
  public String toString() {
    return entrySet().toString();
  }

  protected static class BooleanWrapper {
    public boolean value;
  }

  protected static class Node<K, V> {

    protected static final Object MULTI_STORE_INDICATOR = new Object();

    protected final Object key;
    protected final int keyHash;
    protected final Object value;

    protected final Node<K, V> left;
    protected final Node<K, V> right;
    protected final int height;

    /**
     * @param key     The key or {@link #MULTI_STORE_INDICATOR}
     * @param keyHash The hash of the key
     * @param value   If key is NOT {@link #MULTI_STORE_INDICATOR}, this is the value corresponding to that key. Otherwise, this is the multi-store array.
     *                See {@link #getMultiStore()}
     * @param left    The left child
     * @param right   The right child
     */
    protected Node(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      this.key = key;
      this.keyHash = keyHash;
      this.value = value;
      this.left = left;
      this.right = right;
      this.height = Math.max(height(left), height(right)) + 1;

      assert Math.abs(leftHeight() - rightHeight()) <= 1;
      assert key != MULTI_STORE_INDICATOR
          || (
          value instanceof Object[]
              && ((Object[]) value).length % 2 == 0
              && ((Object[]) value).length >= 4
      );
    }

    public boolean isMultiStore() {
      return key == MULTI_STORE_INDICATOR;
    }

    /**
     * Only call this, if this node actually stores multiple key-value pairs (e.g. {@link #isMultiStore()} returns true).
     *
     * @return The array of key, value pairs. The entries are alternating a key and its corresponding value. Note that the array always has even length and
     *     the length is always >= 4 (e.g. min 2 pairs)
     */
    public Object[] getMultiStore() {
      assert isMultiStore();
      return (Object[]) value;
    }

    public int leftHeight() {
      return left != null ? left.height : 0;
    }

    public int rightHeight() {
      return right != null ? right.height : 0;
    }

    public static int height(Node<?, ?> node) {
      return node == null ? 0 : node.height;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void iterateAll(Node<K, V> node, Action2<? super K, ? super V> action) {
      if (node == null) {
        return;
      }
      iterateAll(node.left, action);
      iterateAll(node.right, action);
      if (node.key == MULTI_STORE_INDICATOR) {
        Object[] multiStore = node.getMultiStore();
        for (int i = 0; i < multiStore.length; i += 2) {
          action.accept((K) multiStore[i], (V) multiStore[i + 1]);
        }
      }
      else {
        action.accept((K) node.key, (V) node.value);
      }
    }

    public static <K, V> boolean containsValue(Object value, Node<K, V> node) {
      if (node == null) {
        return false;
      }
      if (containsValue(value, node.left) || containsValue(value, node.right)) {
        return true;
      }
      if (node.key == MULTI_STORE_INDICATOR) {
        Object[] multiStore = node.getMultiStore();
        for (int i = 1; i < multiStore.length; i += 2) {
          if (Objects.equals(multiStore[i], value)) {
            return true;
          }
        }
        return false;
      }
      return Objects.equals(node.value, value);
    }

    @SuppressWarnings({ "unchecked", "ReturnOfNull" })
    public static <K, V> V get(K key, int keyHash, BooleanWrapper wasKeyPresent, Node<K, V> node) {
      if (node == null) {
        wasKeyPresent.value = false;
        return null;
      }
      if (keyHash < node.keyHash) {
        return get(key, keyHash, wasKeyPresent, node.left);
      }
      else if (keyHash > node.keyHash) {
        return get(key, keyHash, wasKeyPresent, node.right);
      }
      if (node.key == MULTI_STORE_INDICATOR) {
        Object[] multiStore = node.getMultiStore();
        for (int i = 0; i < multiStore.length; i += 2) {
          Object nodeKey = multiStore[i];
          if (!Objects.equals(key, nodeKey)) {
            continue;
          }
          wasKeyPresent.value = true;
          return (V) multiStore[i + 1];
        }
      }
      if (!Objects.equals(key, node.key)) {
        wasKeyPresent.value = false;
        return null;
      }
      wasKeyPresent.value = true;
      return (V) node.value;
    }

    /**
     * Returns a new node with the given key mapped to the value. The boolean indicates if the key was already present. Returns the same node if the key was
     * present and already mapped to the given value.
     */
    protected static <K, V> Node<K, V> withInserted(K key, int keyHash, V value, BooleanWrapper wasKeyPresent, Node<K, V> node) {
      if (node == null) {
        wasKeyPresent.value = false;
        return new Node<>(key, keyHash, value, null, null);
      }
      if (keyHash < node.keyHash) {
        Node<K, V> newLeft = withInserted(key, keyHash, value, wasKeyPresent, node.left);
        if (newLeft == node.left) {
          return node;
        }
        return balanced(
            node.key, node.keyHash, node.value,
            newLeft,
            node.right
        );
      }
      else if (keyHash > node.keyHash) {
        Node<K, V> newRight = withInserted(key, keyHash, value, wasKeyPresent, node.right);
        if (newRight == node.right) {
          return node;
        }
        return balanced(
            node.key, node.keyHash, node.value,
            node.left,
            newRight
        );
      }
      // Same hash. Maybe just a hash collision, maybe we already have the key.

      if (node.key == MULTI_STORE_INDICATOR) {
        Object[] multiStore = node.getMultiStore();
        for (int i = 0; i < multiStore.length; i += 2) {
          Object nodeKey = multiStore[i];
          if (!Objects.equals(key, nodeKey)) {
            continue;
          }
          // Same key, only need to update value
          wasKeyPresent.value = true;
          if (Objects.equals(multiStore[i + 1], value)) {
            return node; // Same value, just return node
          }
          Object[] newMultiStore = multiStore.clone();
          newMultiStore[i + 1] = value;
          return new Node<>(MULTI_STORE_INDICATOR, node.keyHash, newMultiStore, node.left, node.right);
        }
        wasKeyPresent.value = false;
        // New key, need to extend array
        Object[] newMultiStore = Arrays.copyOf(multiStore, multiStore.length + 2);
        newMultiStore[multiStore.length] = key;
        newMultiStore[multiStore.length + 1] = value;
        return new Node<>(MULTI_STORE_INDICATOR, node.keyHash, newMultiStore, node.left, node.right);
      }
      if (Objects.equals(node.key, key)) {
        wasKeyPresent.value = true;

        // Same key, only need to update value
        if (Objects.equals(node.value, value)) {
          return node; // Same value, just return node
        }
        return new Node<>(key, keyHash, value, node.left, node.right);
      }
      // Different key, need to use multi store
      wasKeyPresent.value = false;
      Object[] newMultiStore = new Object[] {
          node.key, node.value,
          key, value
      };
      return new Node<>(MULTI_STORE_INDICATOR, keyHash, newMultiStore, node.left, node.right);
    }

    /**
     * Returns a new node with the given key removed. The boolean indicates if the key was already present. Returns the same node if the key was not
     * present.
     *
     * @param key     The key to remove or {@link #MULTI_STORE_INDICATOR} to remove the full node with the given hash.
     * @param keyHash The hash of the key to remove
     * @param node    The node from which to remove the key. May be null (empty tree)
     * @return The new node with the key removed, or the given node if the key was not present. May be null (e.g. the key was the only key in the tree)
     */
    @SuppressWarnings("ReturnOfNull")
    protected static <K, V> Node<K, V> withRemoved(Object key, int keyHash, Node<K, V> node) {
      if (node == null) {
        return null;
      }
      if (keyHash < node.keyHash) {
        Node<K, V> newLeft = withRemoved(key, keyHash, node.left);
        if (newLeft == node.left) {
          return node;
        }
        return balanced(
            node.key, node.keyHash, node.value,
            newLeft,
            node.right
        );
      }
      else if (keyHash > node.keyHash) {
        Node<K, V> newRight = withRemoved(key, keyHash, node.right);
        if (newRight == node.right) {
          return node;
        }
        return balanced(
            node.key, node.keyHash, node.value,
            node.left,
            newRight
        );
      }
      else {
        if (key != MULTI_STORE_INDICATOR) {
          if (node.key == MULTI_STORE_INDICATOR) {
            Object[] multiStore = node.getMultiStore();
            for (int i = 0; i < multiStore.length; i += 2) {
              Object nodeKey = multiStore[i];
              if (!Objects.equals(key, nodeKey)) {
                continue;
              }
              // Found key, remove it.

              if (multiStore.length == 4) {
                // Was only two keys. No need for multi store anymore
                int otherI;
                if (i == 0) {
                  otherI = 2;
                }
                else {
                  otherI = 0;
                }
                return new Node<>(multiStore[otherI], node.keyHash, multiStore[otherI + 1], node.left, node.right);
              }

              // Remove key from array
              Object[] newMultiStore = new Object[multiStore.length - 2];
              System.arraycopy(multiStore, 0, newMultiStore, 0, i);
              System.arraycopy(multiStore, i + 2, newMultiStore, i, multiStore.length - i - 2);
              return new Node<>(MULTI_STORE_INDICATOR, node.keyHash, newMultiStore, node.left, node.right);
            }
            // Key not found
            return node;
          }
          if (!Objects.equals(node.key, key)) {
            return node;
          }
          // Key present, remove full node below
        }

        // We want to remove the whole node

        if (node.left == null) {
          if (node.right == null) {
            return null;
          }
          return node.right;
        }
        if (node.right == null) {
          return node.left;
        }
        NodeData minData = new NodeData();
        Node<K, V> newRight = withMinRemoved(node.right, minData);
        return balanced(
            minData.key, minData.keyHash, minData.value,
            node.left,
            newRight
        );
      }
    }

    protected static class NodeData {

      public Object key;
      public int keyHash;
      public Object value;

    }

    protected static <K, V> Node<K, V> withMinRemoved(Node<K, V> node, NodeData minData) {
      if (node.left == null) {
        minData.key = node.key;
        minData.keyHash = node.keyHash;
        minData.value = node.value;
        return withRemoved(node.key, node.keyHash, node);
      }
      return balanced(
          node.key, node.keyHash, node.value,
          withMinRemoved(node.left, minData),
          node.right
      );
    }

    protected static <K, V> Node<K, V> balanced(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      int leftHeight = Node.height(left);
      int rightHeight = Node.height(right);
      if (leftHeight - 2 == rightHeight) {
        if (left.leftHeight() < left.rightHeight()) {
          return leftRotateLeftThenRightRotateTop(key, keyHash, value, left, right);
        }
        else {
          return rightRotated(key, keyHash, value, left, right);
        }
      }
      else if (leftHeight == rightHeight - 2) {
        if (right.rightHeight() < right.leftHeight()) {
          return rightRotateRightThenLeftRotateTop(key, keyHash, value, left, right);
        }
        else {
          return leftRotated(key, keyHash, value, left, right);
        }
      }
      else {
        assert Math.abs(leftHeight - rightHeight) < 2;
        return new Node<>(key, keyHash, value, left, right);
      }
    }

    protected static <K, V> Node<K, V> leftRotated(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      return new Node<>(
          right.key, right.keyHash, right.value,
          new Node<>(
              key, keyHash, value,
              left,
              right.left
          ),
          right.right
      );
    }

    protected static <K, V> Node<K, V> rightRotated(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      return new Node<>(
          left.key, left.keyHash, left.value,
          left.left,
          new Node<>(
              key, keyHash, value,
              left.right,
              right
          )
      );
    }

    /**
     * Performs a {@link #leftRotated(Object, int, Object, Node, Node) left rotation} on {@link #left}, and then performs a
     * {@link #rightRotated(Object, int, Object, Node, Node) right rotation} on the top level node (the one which would be constructed with the given
     * arguments)
     */
    protected static <K, V> Node<K, V> leftRotateLeftThenRightRotateTop(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      return new Node<>(
          left.right.key, left.right.keyHash, left.right.value,
          new Node<>(
              left.key, left.keyHash, left.value,
              left.left,
              left.right.left
          ),
          new Node<>(
              key, keyHash, value,
              left.right.right,
              right
          )
      );
    }

    /**
     * Performs a {@link #rightRotated(Object, int, Object, Node, Node) right rotation} on {@link #right}, and then performs a
     * {@link #leftRotated(Object, int, Object, Node, Node) left rotation} on the top level node (the one which would be constructed with the given
     * arguments)
     */
    protected static <K, V> Node<K, V> rightRotateRightThenLeftRotateTop(Object key, int keyHash, Object value, Node<K, V> left, Node<K, V> right) {
      return new Node<>(
          right.left.key, right.left.keyHash, right.left.value,
          new Node<>(
              key, keyHash, value,
              left,
              right.left.left
          ),
          new Node<>(
              right.key, right.keyHash, right.value,
              right.left.right,
              right.right
          )
      );
    }

  }

  protected static abstract class ViewSet<T> implements FSet<T> {

    protected SoftReference<FSet<T>> realCopy = new SoftReference<>(null);

    protected FSet<T> getRealCopy() {
      // Even with this non-final variable, this is still immutable. This is also still thread-safe, though it could happen the Set is copied multiple
      // times.
      FSet<T> val = this.realCopy.get();
      if (val == null) {
        val = new FHashSet<>(this);
        realCopy = new SoftReference<>(val);
      }
      return val;
    }

  }

  protected static class EntryIterator<K, V> implements Iterator<Map.Entry<K, V>> {

    // The node stack contains all nodes from the root to the next node to be read.
    protected final Node<K, V>[] nodeStack;
    protected int nodeStackI;

    protected int multiStackNextI;

    @SuppressWarnings("unchecked")
    protected EntryIterator(Node<K, V> root) {
      this.nodeStack = new Node[Node.height(root)];
      this.nodeStackI = -1;
      if (root != null) {
        this.nodeStackI = 0;
        this.nodeStack[0] = root;
        goToBottom();
      }
    }

    protected void goToBottom() {
      Node<K, V> currentNode = this.nodeStack[this.nodeStackI];
      while (true) {
        if (currentNode.left != null) {
          currentNode = currentNode.left;
        }
        else if (currentNode.right != null) {
          currentNode = currentNode.right;
        }
        else {
          return;
        }
        this.nodeStackI++;
        this.nodeStack[this.nodeStackI] = currentNode;
      }
    }

    @Override
    public boolean hasNext() {
      return this.nodeStackI != -1;
    }

    /**
     * Update the internal state to point at the next node
     */
    protected void advanceNode() {
      // Go to parent
      Node<K, V> prevNode = this.nodeStack[this.nodeStackI];
      this.nodeStack[this.nodeStackI] = null;
      this.nodeStackI--;
      if (this.nodeStackI == -1) {
        // Finished visiting
        return;
      }
      Node<K, V> currentNode = this.nodeStack[this.nodeStackI];

      assert currentNode != null;
      if (currentNode.left == prevNode) {
        // We came from left child up. Go to right child and follow left children to bottom

        if (currentNode.right == null) {
          // No right child. Stay at node
          return;
        }

        currentNode = currentNode.right;
        this.nodeStackI++;
        this.nodeStack[this.nodeStackI] = currentNode;

        goToBottom();
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entry<K, V> next() {
      if (this.nodeStackI == -1) {
        throw new NoSuchElementException();
      }
      Node<K, V> nextNode = this.nodeStack[this.nodeStackI];
      if (!nextNode.isMultiStore()) {
        advanceNode();
        return new SimpleEntry<>(
            (K) nextNode.key,
            (V) nextNode.value
        );
      }
      Object[] multiStore = nextNode.getMultiStore();
      Entry<K, V> entry = new SimpleEntry<>(
          (K) multiStore[multiStackNextI],
          (V) multiStore[multiStackNextI + 1]
      );
      multiStackNextI += 2;
      if (multiStackNextI >= multiStore.length) {
        advanceNode();
        multiStackNextI = 0;
      }
      return entry;
    }

  }

  protected class EntrySet extends ViewSet<Map.Entry<K, V>> {

    @Override
    public FSet<Entry<K, V>> with(Entry<K, V> element) {
      return getRealCopy().with(element);
    }

    @Override
    public FSet<Entry<K, V>> withAll(FCollection<? extends Entry<K, V>> other) {
      return getRealCopy().withAll(other);
    }

    @Override
    public FSet<Entry<K, V>> without(Object element) {
      return getRealCopy().without(element);
    }

    @Override
    public FSet<Entry<K, V>> withoutAll(FCollection<?> other) {
      return getRealCopy().withoutAll(other);
    }

    @Override
    public int size() {
      return FHashMap.this.size;
    }

    @Override
    public boolean isEmpty() {
      return FHashMap.this.root == null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object element) {
      if (!(element instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> entry = (Entry<?, ?>) element;
      K key = (K) entry.getKey();
      BooleanWrapper wasKeyPresent = new BooleanWrapper();
      V value = Node.get(key, Objects.hashCode(key), wasKeyPresent, FHashMap.this.root);
      if (!wasKeyPresent.value) {
        return false;
      }
      return Objects.equals(value, entry.getValue());
    }

    @Override
    public Set<Entry<K, V>> toJava() {
      return FHashMap.this.toJava().entrySet();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator<>(FHashMap.this.root);
    }

    @Override
    public void forEach(Consumer<? super Entry<K, V>> action) {
      Node.iterateAll(FHashMap.this.root, (key, value) -> action.accept(new SimpleEntry<>(key, value)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FSet<?>)) {
        return false;
      }
      FSet<?> otherSet = (FSet<?>) obj;
      if (otherSet.size() != this.size()) {
        return false;
      }
      for (Object entryObj : otherSet) {
        if (!(entryObj instanceof Map.Entry<?, ?>)) {
          return false;
        }
        Map.Entry<?, ?> entry = (Entry<?, ?>) entryObj;
        BooleanWrapper wasKeyPresent = new BooleanWrapper();
        V ourValue = Node.get((K) entry.getKey(), Objects.hashCode(entry.getKey()), wasKeyPresent, FHashMap.this.root);
        if (!wasKeyPresent.value || !Objects.equals(entry.getValue(), ourValue)) {
          return false;
        }
      }
      // Since we have all the entries of the other map, and we have the same size, we are equal
      return true;
    }

    @Override
    public int hashCode() {
      class IntWrapper {

        int value;

      }
      IntWrapper intWrapper = new IntWrapper();
      // iterateAll costs less than using an iterator
      Node.iterateAll(
          FHashMap.this.root,
          (key, value) -> intWrapper.value += Objects.hashCode(key) ^ Objects.hashCode(value));
      return intWrapper.value;
    }

    @Override
    public String toString() {
      if (isEmpty()) {
        return "{}";
      }
      StringBuilder res = new StringBuilder();
      res.append("{");
      Node.iterateAll(FHashMap.this.root, (key, value) -> res.append(key).append("=").append(value).append(", "));
      res.delete(res.length() - 2, res.length());
      res.append("}");
      return res.toString();
    }

  }

  protected class KeySet extends ViewSet<K> {

    @Override
    public FSet<K> with(K element) {
      return getRealCopy().with(element);
    }

    @Override
    public FSet<K> withAll(FCollection<? extends K> other) {
      return getRealCopy().withAll(other);
    }

    @Override
    public FSet<K> without(Object element) {
      return getRealCopy().without(element);
    }

    @Override
    public FSet<K> withoutAll(FCollection<?> other) {
      return getRealCopy().withoutAll(other);
    }

    @Override
    public int size() {
      return FHashMap.this.size;
    }

    @Override
    public boolean isEmpty() {
      return FHashMap.this.root == null;
    }

    @Override
    public boolean contains(Object element) {
      return FHashMap.this.containsKey(element);
    }

    @Override
    public Set<K> toJava() {
      return FHashMap.this.toJava().keySet();
    }

    @Override
    public Iterator<K> iterator() {
      return new Iterator<>() {

        final Iterator<Map.Entry<K, V>> original = FHashMap.this.entrySet().iterator();

        @Override
        public boolean hasNext() {
          return original.hasNext();
        }

        @Override
        public K next() {
          return original.next().getKey();
        }
      };
    }

    @Override
    public void forEach(Consumer<? super K> action) {
      Node.iterateAll(FHashMap.this.root, (key, value) -> action.accept(key));
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FSet<?>)) {
        return false;
      }
      FSet<?> otherSet = (FSet<?>) obj;
      if (otherSet.size() != this.size()) {
        return false;
      }
      for (Object value : otherSet) {
        if (!FHashMap.this.containsKey(value)) {
          return false;
        }
      }
      // Since we have all the values of the other set, and we have the same size, we are equal
      return true;
    }

    @Override
    public int hashCode() {
      class IntWrapper {

        int value;

      }
      IntWrapper intWrapper = new IntWrapper();
      // iterateAll costs less than using an iterator
      Node.iterateAll(
          FHashMap.this.root,
          (key, value) -> intWrapper.value += Objects.hashCode(key)
      );
      return intWrapper.value;
    }

    @Override
    public String toString() {
      if (isEmpty()) {
        return "{}";
      }
      StringBuilder res = new StringBuilder();
      res.append("{");
      Node.iterateAll(FHashMap.this.root, (key, value) -> res.append(key).append(", "));
      res.delete(res.length() - 2, res.length());
      res.append("}");
      return res.toString();
    }

  }

  protected class ValueCollection implements FCollection<V> {

    @Override
    public int size() {
      return FHashMap.this.size;
    }

    @Override
    public boolean isEmpty() {
      return FHashMap.this.size == 0;
    }

    @Override
    public boolean contains(Object element) {
      return FHashMap.this.containsValue(element);
    }

    @Override
    public Collection<V> toJava() {
      return FHashMap.this.toJava().values();
    }

    @Override
    public Iterator<V> iterator() {
      return new Iterator<>() {

        final Iterator<Map.Entry<K, V>> original = FHashMap.this.entrySet().iterator();

        @Override
        public boolean hasNext() {
          return original.hasNext();
        }

        @Override
        public V next() {
          return original.next().getValue();
        }
      };
    }

    @Override
    public void forEach(Consumer<? super V> action) {
      Node.iterateAll(FHashMap.this.root, (key, value) -> action.accept(value));
    }

    // No equals/hashcode for pure collection (e.g. because if this.equals(someList) == true, then someList.equals(this) would still be false)

    @Override
    public String toString() {
      if (isEmpty()) {
        return "()";
      }
      StringBuilder res = new StringBuilder();
      res.append("(");
      Node.iterateAll(FHashMap.this.root, (key, value) -> res.append(value).append(", "));
      res.delete(res.length() - 2, res.length());
      res.append(")");
      return res.toString();
    }

  }

}
