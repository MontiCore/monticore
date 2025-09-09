// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunitliterals.types3;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunit.siunitliterals._visitor.SIUnitLiteralsVisitor2;
import de.monticore.siunit.siunits.util.ASTSIUnit2SymTypeExprConverter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.AbstractTypeVisitor;

public class SIUnitLiteralsTypeVisitor extends AbstractTypeVisitor
    implements SIUnitLiteralsVisitor2 {

  @Override
  public void endVisit(ASTSIUnitLiteral siLit) {
    SymTypeExpression type;
    SymTypeOfSIUnit siUnit =
        ASTSIUnit2SymTypeExprConverter.createSIUnit(siLit.getSIUnit());
    SymTypeExpression litType =
        getType4Ast().getPartialTypeOfExpr(siLit.getNumericLiteral());
    if (litType.isObscureType()) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      type = SymTypeExpressionFactory.createNumericWithSIUnit(siUnit, litType);
    }
    type4Ast.setTypeOfExpression(siLit, type);
  }

}
