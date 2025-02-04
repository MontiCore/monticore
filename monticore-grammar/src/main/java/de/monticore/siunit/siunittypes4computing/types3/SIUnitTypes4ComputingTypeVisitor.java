// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunittypes4computing.types3;

import de.monticore.siunit.siunits.util.ASTSIUnit2SymTypeExprConverter;
import de.monticore.siunit.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunit.siunittypes4computing._visitor.SIUnitTypes4ComputingVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.AbstractTypeVisitor;

public class SIUnitTypes4ComputingTypeVisitor extends AbstractTypeVisitor
    implements SIUnitTypes4ComputingVisitor2 {

  @Override
  public void endVisit(ASTSIUnitType4Computing siu4c) {
    SymTypeExpression type;
    SymTypeOfSIUnit siUnit =
        ASTSIUnit2SymTypeExprConverter.createSIUnit(siu4c.getSIUnit());
    SymTypeExpression primType =
        getType4Ast().getPartialTypeOfTypeId(siu4c.getMCPrimitiveType());
    if (primType.isObscureType()) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      type = SymTypeExpressionFactory.createNumericWithSIUnit(siUnit, primType);
    }
    type4Ast.setTypeOfTypeIdentifier(siu4c, type);
  }

}
