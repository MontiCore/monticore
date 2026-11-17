/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals.types3;

import de.monticore.temporal.detemporals._ast.ASTDEAlphanumericDate;
import de.monticore.temporal.detemporals._ast.ASTDEDateTime;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.detemporals._visitor.DETemporalsVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.temporal.TemporalSymTypeFactory;

/**
 * Visitor for Derivation of SymType from DETemporals.mc4
 */
public class DETemporalsTypeVisitor extends AbstractTypeVisitor
    implements DETemporalsVisitor2 {

  @Override
  public void endVisit(ASTDEAlphanumericDate lit) {
    getType4Ast().setTypeOfExpression(lit,
        TemporalSymTypeFactory.createTimePoint()
    );
  }

  @Override
  public void endVisit(ASTDETime lit) {
    getType4Ast().setTypeOfExpression(lit,
        TemporalSymTypeFactory.createDayTime()
    );
  }

  @Override
  public void endVisit(ASTDEDateTime lit) {
    getType4Ast().setTypeOfExpression(lit,
        TemporalSymTypeFactory.createTimePoint()
    );
  }

}
