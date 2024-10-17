/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * This visitor calculates the variables contained in a given expression.
 */
public class FindDependVarsVisitor implements
        ExpressionsBasisVisitor2 {

  protected List<ASTMatchingObject> lhsObjects;
  public Set<ASTMatchingObject> dependVars;

  public FindDependVarsVisitor(List<ASTMatchingObject> lhsObjects){
    super();
    this.lhsObjects = lhsObjects;
    dependVars = new HashSet<>();
  }

  @Override
  public void visit(ASTNameExpression node) {
    for(ASTMatchingObject o : lhsObjects){
      if(node.getName().equals(o.getObjectName())){
        dependVars.add(o);
        break;
      }
    }
  }

}
