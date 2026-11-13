// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunitliterals.types3;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunit.siunitliterals._visitor.SIUnitLiteralsVisitor2;
import de.monticore.siunit.siunits.util.ASTSIUnit2SymTypeExprConverter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;

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
      type = SymTypeExpressionFactory.createNumericWithSIUnit(
          siUnit, getValueType(litType)
      );
    }
    type4Ast.setTypeOfExpression(siLit, type);
  }

  /**
   * integral values are stored as double,
   * as converting between units, e.g., {@code 3m -> 0.003km},
   * would otherwise lose information
   */
  protected SymTypeExpression getValueType(SymTypeExpression litType) {
    if (SymTypeRelations.isIntegralType(litType)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
    }
    return litType;
  }

}
