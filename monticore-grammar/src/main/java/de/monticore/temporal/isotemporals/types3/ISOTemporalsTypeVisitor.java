/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.types3;

import de.monticore.temporal.isotemporals._ast.ASTBasicWeekDate;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsVisitor2;
import de.monticore.temporal.temporalbasis.types3.TemporalBasisTypeVisitor;

/**
 * Derives the temporal type of the ISO 8601 representations
 * that can be used as literals without escaping,
 * e.g., {@code 2015-04-01T12:30} is a TimePoint.
 */
public class ISOTemporalsTypeVisitor extends TemporalBasisTypeVisitor
    implements ISOTemporalsVisitor2 {

  @Override
  public void endVisit(ASTISODateTime lit) {
    setTypeOfInstantLiteral(lit, lit);
  }

  @Override
  public void endVisit(ASTBasicWeekDate lit) {
    setTypeOfInstantLiteral(lit, lit);
  }

}
