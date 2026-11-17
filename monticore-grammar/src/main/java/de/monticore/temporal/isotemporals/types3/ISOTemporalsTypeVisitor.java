/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.types3;

import de.monticore.temporal.isotemporals._ast.ASTBasicWeekDate;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.temporal.TemporalSymTypeFactory;

/**
 * Visitor for Derivation of SymType from Literals
 */
public class ISOTemporalsTypeVisitor extends AbstractTypeVisitor
    implements ISOTemporalsVisitor2 {

  @Override
  public void endVisit(ASTBasicWeekDate lit) {
    getType4Ast().setTypeOfExpression(lit,
        TemporalSymTypeFactory.createTimePoint());
  }

  @Override
  public void endVisit(ASTISODateTime lit) {
    getType4Ast().setTypeOfExpression(lit,
        TemporalSymTypeFactory.createTimePoint());
  }

}
