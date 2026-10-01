/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * A sliding range, read-only window of list.
 * Alternative to myList = new ArrayList(list);
 * myList.remove(0);
 * Calling {@link #remove(int)} of this class instead moves the read-window ahead.
 * Does not support updates to the underlying list.
 *
 * @param <T> type of the list
 */

public class FastLookupList<T> extends AbstractList<T> {
  
  protected List<T> list;

  /**
   * Constructs a {@link FastLookupList} wrapping the specified list.
   *
   * @param list the underlying list to wrap
   */
  public FastLookupList(List<T> list) {
    this.list = list;
    this.size = list.size();
  }
  
  /**
   * Constructs a {@link FastLookupList} wrapping the specified list with a given removal counter.
   *
   * @param list the underlying list to wrap
   * @param removalCounter the initial removal counter to start from
   */
  public FastLookupList(List<T> list, int removalCounter) {
    this(list);
    this.removalCounter = removalCounter;
  }
  
  protected int removalCounter = 0;
  protected int size;

  /**
   * Resets the removal counter to zero, allowing access to the beginning of the list again.
   */
  public void reset() {
    removalCounter = 0;
  }
  
  /**
   * Retrieves the element at the specified index from the underlying list,
   * adjusted by the current removal counter offset.
   *
   * @param index the index of the element to retrieve
   * @return the element at the adjusted index
   * @throws ConcurrentModificationException if the underlying list size has changed
   * @throws IndexOutOfBoundsException if the adjusted index is out of bounds
   */
  @Override
  public T get(int index) {
    if (list.size() != size) {
      throw new ConcurrentModificationException("FastLookupList size changed");
    }
    return this.list.get(index + removalCounter);
  }
  
  /**
   * Removes and returns the element at the specified index.
   * Only index 0 can be removed; removing from other indices throws an exception.
   * Moving the read-window ahead by incrementing the internal removal counter.
   *
   * @param index the index of the element to remove (must be 0)
   * @return {@code null} (the element is not actually returned from the underlying list)
   * @throws IllegalArgumentException if index is not 0
   * @throws ConcurrentModificationException if the underlying list size has changed
   */
  @Override
  public T remove(int index) {
    if (index != 0) {
      throw new IllegalArgumentException("You may only remove index 0");
    }
    removalCounter++;
    return null;
  }
  
  @Override
  public int size() {
    return size - removalCounter;
  }
  
  /**
   * Checks if this list is empty.
   *
   * @return {@code true} if the list is empty, {@code false} otherwise
   * @throws ConcurrentModificationException if the underlying list size has changed
   */
  @Override
  public boolean isEmpty() {
    if (list.size() != size) {
      throw new ConcurrentModificationException("FastLookupList size changed");
    }
    return removalCounter == size;
  }

  /**
   * Creates a copy of this {@link FastLookupList} sharing the same underlying list
   * and current removal counter position.
   *
   * @return a new {@link FastLookupList} with the same state
   */
  public FastLookupList<T> matchCopy() {
    return new FastLookupList<>(this.list, this.removalCounter);
  }
  
  @Override
  public String toString() {
    return "FastLookupList{" + removalCounter + "/ " + size + ": " + list.subList(removalCounter,
        size) + "}";
  }
}
