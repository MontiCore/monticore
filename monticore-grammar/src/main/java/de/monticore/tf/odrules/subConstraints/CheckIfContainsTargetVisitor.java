/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.IVisitor;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * This visitor determines if if an Expression contains a given Expression targetNode.
 */
public class CheckIfContainsTargetVisitor implements
        IVisitor
{

  protected ASTExpression targetNode;
  public boolean containsTarget = false;

  public CheckIfContainsTargetVisitor(ASTExpression targetNode){
    super();
    this.targetNode = targetNode;
    containsTarget = false;
  }

  @Override
    public void visit(ASTNode node) {

     if(node.equals(targetNode)){
       containsTarget= true;
     }
  }
}
