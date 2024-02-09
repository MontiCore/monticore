// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunitliterals.types3;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunit.siunitliterals._visitor.SIUnitLiteralsVisitor2;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;

public class SIUnitLiteralsTypeVisitor extends AbstractTypeVisitor
    implements SIUnitLiteralsVisitor2 {

  @Override
  public void visit(ASTSIUnitLiteral node) {
    SymTypeExpressionFactory.creat
    node.getSIUnit()
  }

}
