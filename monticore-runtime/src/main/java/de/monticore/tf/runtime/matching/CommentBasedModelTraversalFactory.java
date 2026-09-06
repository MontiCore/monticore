/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.visitor.ITraverser;

public class CommentBasedModelTraversalFactory extends ModelTraversalFactory {
  
  private static CommentBasedModelTraversalFactory instance;
  
  public static CommentBasedModelTraversalFactory getInstance() {
    if (instance == null) {
      instance = new CommentBasedModelTraversalFactory();
    }
    return instance;
  }
  
  /**
   * @return a {@link ModelTraversal} for the given model
   */
  public ModelTraversalVisitor createVisitor(ModelTraversal<?> modelTraversal) {
    return new CommentBasedModelTraversalVisitor(modelTraversal);
  }

  public <E extends ITraverser> ModelTraversal<E> create(java.util.function.Supplier<E> traverserSupplier){
    return this.create(traverserSupplier.get());
  }

  public <E extends ITraverser> ModelTraversal<E> create(E traverser){
    ModelTraversal<E> modelTraversal = new CommentBasedModelTraversal<>(traverser);
    traverser.add4IVisitor(createVisitor(modelTraversal));
    return modelTraversal;
  }
}
