/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import de.monticore.rte.actions.Action2;

import java.util.Map;
import java.util.Objects;

/**
 * A {@link Map} in the style of an {@link FCollection}
 *
 * @param <K> The type of keys in this map
 * @param <V> The type of values in this map
 */
public interface FMap<K, V> {

  /**
   * This has the same semantics as {@link Map#size()}
   */
  int size();

  /**
   * This has the same semantics as {@link Map#isEmpty()}
   */
  boolean isEmpty();

  /**
   * This has the same semantics as {@link Map#containsKey(Object)}
   */
  boolean containsKey(Object key);

  /**
   * This has the same semantics as {@link Map#containsValue(Object)}
   */
  boolean containsValue(Object value);

  /**
   * This has the same semantics as {@link Map#get(Object)}
   */
  V get(Object key);

  /**
   * Returns a copy of this map but with the given key mapped to the given value. If this already had the given key mapped to the given value, the returned
   * map is equal to this map. If this already contains the given key, but mapped to a different value, that value is overridden in the returned map.
   *
   * @param key   The key to map
   * @param value The value to store
   * @return The map with the given entry applied
   */
  FMap<K, V> with(K key, V value);

  /**
   * Returns a copy of this map but with all entries of the given map applied. If this already had all the entries with the same values, the returned map is
   * equal to this map. If this already contained any key of the given map, but mapped to a different value, that value is overridden in the returned map.
   *
   * @param other The map whose entries should be added
   * @return The map with the given map's entries applied
   */
  FMap<K, V> withAll(FMap<K, V> other);

  /**
   * Returns a copy of this map but with the mapping for the given key removed. If this does not have a mapping for the given key, the returned map is equal
   * to this map
   *
   * @param key The key to remove
   * @return The map without the given key
   */
  FMap<K, V> without(Object key);

  /**
   * Returns a copy of this map but with the mappings for the given keys removed. If this does not have a mapping for any of the given keys, the returned map
   * is equal to this map
   *
   * @param keys The keys to remove
   * @return The map without the given keys
   */
  FMap<K, V> withoutAll(FCollection<?> keys);

  /**
   * @return A {@link FSet} which contains all the keys of this map.
   */
  FSet<K> keySet();

  /**
   * @return A {@link FCollection} which contains all the values stored in this map.
   */
  FCollection<V> values();

  /**
   * @return A {@link FSet} containing entries of all the keys and their associated values stored in this map.
   */
  FSet<Map.Entry<K, V>> entrySet();

  /**
   * This has the same semantics as {@link Map#hashCode()}
   */
  @Override
  int hashCode();

  /**
   * This has the same semantics as {@link Map#equals(Object)} (just inside the FMap system. A {@link FMap} is never equal to a {@link Map})!
   */
  @Override
  boolean equals(Object obj);

  /**
   * @return An unmodifiable {@link Map} containing the same key-value mappings as this map.
   */
  Map<K, V> toJava();

  /**
   * This has the same semantics as {@link Map#getOrDefault(Object, Object)}
   */
  default V getOrDefault(Object key, V defaultValue) {
    V value = get(key);
    if (value != null || containsKey(key)) {
      return value;
    }
    return defaultValue;
  }

  /**
   * This has the same semantics as
   * {@link Map#forEach(java.util.function.BiConsumer)}
   */
  void forEach(Action2<? super K, ? super V> action);

  /**
   * Returns a copy of this map but with the given entry removed. If this does not have a mapping for the given key, or the key is currently mapped to a
   * different value, the returned map is equal to this map
   *
   * @param key   The key to remove
   * @param value The value to remove
   * @return The map without the given entry
   */
  default FMap<K, V> without(Object key, Object value) {
    V currentValue = get(key);
    if (!Objects.equals(currentValue, value) || (currentValue == null && !containsKey(key))) {
      return this;
    }
    return without(key);
  }

  /**
   * Returns an empty map
   *
   * @param <K> The type of keys in the map
   * @param <V> The type of values in the map
   * @return A {@link FMap} containing no elements.
   */
  static <K, V> FMap<K, V> of() {
    @SuppressWarnings("unchecked")
    FMap<K, V> m = (FMap<K, V>) FHashMap.EMPTY;
    return m;
  }

  // todo: to be extended, currently only 3 Entries are supported...
  // perhaps internal `of` that takes two lists?
  // also, `of(Map<K,V>)` to create one from a java.util.Map.

  /**
   * Returns a new map with the given entry
   *
   * @param key   The key of the only entry
   * @param value The value for the given key
   * @param <K>   The type of keys in the map
   * @param <V>   The type of values in the map
   * @return A {@link FMap} containing one element.
   */
  static <K, V> FMap<K, V> of(K key, V value) {
    return FMap.<K, V> of().with(key, value);
  }

  /**
   * Returns a new map with the given entries
   *
   * @param k1  The key for the first entry
   * @param v1  The value for the first entry
   * @param k2  The key for the second entry
   * @param v2  The value for the second entry
   * @param <K> The type of keys in the map
   * @param <V> The type of values in the map
   * @return A {@link FMap} containing two elements.
   * @throws IllegalArgumentException If any of the keys are equal (duplicates)
   */
  static <K, V> FMap<K, V> of(K k1, V v1, K k2, V v2) {
    FMap<K, V> res = FMap.<K, V> of().with(k1, v1).with(k2, v2);
    if (res.size() != 2) {
      throw new IllegalArgumentException("Duplicate key provided: " + k1);
    }
    return res;
  }

  /**
   * Returns a new map with the given entries
   *
   * @param k1  The key for the first entry
   * @param v1  The value for the first entry
   * @param k2  The key for the second entry
   * @param v2  The value for the second entry
   * @param k3  The key for the third entry
   * @param v3  The value for the third entry
   * @param <K> The type of keys in the map
   * @param <V> The type of values in the map
   * @return A {@link FMap} containing three elements.
   * @throws IllegalArgumentException If any of the keys are equal (duplicates)
   */
  static <K, V> FMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    FMap<K, V> res = FMap.<K, V> of().with(k1, v1).with(k2, v2).with(k3, v3);
    if (res.size() != 3) {
      if (Objects.equals(k1, k2) || Objects.equals(k1, k3)) {
        throw new IllegalArgumentException("Duplicate key provided: " + k1);
      }
      else {
        throw new IllegalArgumentException("Duplicate key provided: " + k2);
      }
    }
    return res;
  }

}
