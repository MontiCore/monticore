package de.monticore.tf.runtime.inc;

import de.monticore.visitor.ITraverser;

public interface IModelIndex<E extends ITraverser> extends IIncrementalListener {
  
  void registerIntoTraverser(E traverser);
  
  default void finalizeInitialization() {}
}
