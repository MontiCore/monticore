/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.visitor.IVisitor;

public class CommentBasedModelTraversalVisitor extends ModelTraversalVisitor implements IVisitor {
  
  protected CommentBasedModelTraversalVisitor(ModelTraversal<?> modelTraversal) {
    super(modelTraversal);
  }
}
