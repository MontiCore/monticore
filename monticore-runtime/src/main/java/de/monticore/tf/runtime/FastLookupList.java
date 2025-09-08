package de.monticore.tf.runtime;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class FastLookupList<T> extends AbstractList<T> {
  
  protected List<T> list;
  
  public FastLookupList(List<T> list) {
    this.list = list;
    this.size = list.size();
  }
  
  public FastLookupList(List<T> list, int removalCounter) {
    this(list);
    this.removalCounter = removalCounter;
  }
  
  protected int removalCounter = 0;
  protected int size;
  
  public void reset() {
    removalCounter = 0;
  }
  
  @Override
  public T get(int index) {
    if (list.size() != size) {
      throw new ConcurrentModificationException("FastLookupList size changed");
    }
    return this.list.get(index + removalCounter);
  }
  
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
  
  @Override
  public boolean isEmpty() {
    if (list.size() != size) {
      throw new ConcurrentModificationException("FastLookupList size changed");
    }
    return removalCounter == size;
  }
  
  public FastLookupList<T> matchCopy() {
    return new FastLookupList<>(this.list, this.removalCounter);
  }
  
  @Override
  public String toString() {
    return "FastLookupList{" + removalCounter + "/ " + size + ": " + list.subList(removalCounter,
        size) + "}";
  }
}
