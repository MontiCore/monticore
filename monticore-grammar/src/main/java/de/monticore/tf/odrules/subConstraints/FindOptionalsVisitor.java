/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * This visitor calculates if there are any elements with stereotype 'optional' or 'null' contained in the given expression.
 */
public class FindOptionalsVisitor implements
        ExpressionsBasisVisitor2 {

  protected List<ASTMatchingObject> lhsObjects;
  protected HierarchyHelper hierarchyHelper;
  public Set<ASTMatchingObject> optVars;

  public FindOptionalsVisitor(List<ASTMatchingObject> lhsObjects, HierarchyHelper hierarchyHelper){
    super();
    this.hierarchyHelper = hierarchyHelper;
    this.lhsObjects = lhsObjects;
    optVars = new HashSet<>();
  }

  @Override
  public void visit(ASTNameExpression node) {

    for(ASTMatchingObject o : lhsObjects){
      if(node.getName().equals(o.getObjectName())){
        if(hierarchyHelper.isWithinOptionalStructure(o.getObjectName())
            || hierarchyHelper.isWithinNegativeStructure(o.getObjectName())){
          optVars.add(o);
        }
      }
    }

  }


}
