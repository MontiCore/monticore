/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.IVisitor;

import java.util.LinkedList;

/**
 * @deprecated This visitor should no longer be used. Pattern matching now relies on
 * incremental indices instead of {@link ModelTraversal}.
 */
@Deprecated(since = "7.9.0")
public class ModelTraversalVisitor implements IVisitor {

  protected final ModelTraversal<?> modelTraversal;

  protected ModelTraversalVisitor(
          ModelTraversal<?> modelTraversal) {
    this.modelTraversal = modelTraversal;
  }

  @Override
  public void visit(ASTNode node) {
      if (!modelTraversal.currentparents.isEmpty()) {
        modelTraversal.parents.put(node, modelTraversal.currentparents.peek());
      }
    modelTraversal.currentparents.push(node);
    String cName = node.getClass().getCanonicalName();
    modelTraversal.all.add(node);
    if (!modelTraversal.cName2instances.containsKey(cName)) {
      modelTraversal.cName2instances.put(cName, new LinkedList<>());
    }
    modelTraversal.cName2instances.get(cName).add(node);
  }

  @Override
  public void endVisit(ASTNode node) {
    modelTraversal.currentparents.pop();
  }

}
