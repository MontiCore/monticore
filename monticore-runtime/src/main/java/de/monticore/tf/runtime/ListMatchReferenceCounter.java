package de.monticore.tf.runtime;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;

import java.util.IdentityHashMap;
import java.util.Map;

public class ListMatchReferenceCounter {

  protected static class MutableInt {

    private int value;

    public MutableInt(int value) {
      this.value = value;
    }

    public void inc() {
      this.value++;
    }

    public void dec() {
      this.value--;
    }

    public int getValue() {
      return this.value;
    }
  }

  protected Map<ASTNode, MutableInt> refs = new IdentityHashMap<>();

  public ListMatchReferenceCounter() {
  }

  public ListMatchReferenceCounter(ListMatchReferenceCounter other) {
    Preconditions.checkNotNull(other);
    other.refs.forEach((k, v) -> refs.put(k, new MutableInt(v.getValue())));
  }

  public void inc(ASTNode node) {
    if (node == null) {
      return;
    }
    refs.putIfAbsent(node, new MutableInt(0));
    refs.get(node).inc();
  }

  public void dec(ASTNode node) {
    if (node == null) {
      return;
    }
    MutableInt referenceCount = this.refs.get(node);
    if (referenceCount != null) {
      referenceCount.dec();
      if (referenceCount.getValue() < 0) {
        throw new IllegalStateException("Reference count for " + node + " is negative");
      }
    } else {
      throw new IllegalStateException("Reference count for " + node + " is unknown");
    }
  }

  public boolean isMatchedBy(ASTNode node) {
    Preconditions.checkNotNull(node);
    MutableInt referenceCount = this.refs.get(node);
    if (referenceCount == null) {
      return false;
    }
    return referenceCount.getValue() > 0;
  }
}
