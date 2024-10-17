// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunittypes4math.types3;

import de.monticore.siunit.siunits.util.ASTSIUnit2SymTypeExprConverter;
import de.monticore.siunit.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.siunit.siunittypes4math._visitor.SIUnitTypes4MathVisitor2;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.AbstractTypeVisitor;

public class SIUnitTypes4MathTypeVisitor extends AbstractTypeVisitor
    implements SIUnitTypes4MathVisitor2 {

  @Override
  public void endVisit(ASTSIUnitType siUnitType) {
    SymTypeOfSIUnit type =
        ASTSIUnit2SymTypeExprConverter.createSIUnit(siUnitType.getSIUnit());
    type4Ast.setTypeOfTypeIdentifier(siUnitType, type);
  }

}
