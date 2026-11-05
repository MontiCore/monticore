/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals.types3;

import de.monticore.temporal.detemporals._ast.ASTDEAlphanumericDate;
import de.monticore.temporal.detemporals._ast.ASTDEDateTime;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.detemporals._visitor.DETemporalsVisitor2;
import de.monticore.temporal.temporalbasis.types3.TemporalBasisTypeVisitor;

/**
 * Derives the temporal type of the German representations
 * that can be used as literals without escaping,
 * e.g., {@code 12:30 Uhr} is a DayTime.
 */
public class DETemporalsTypeVisitor extends TemporalBasisTypeVisitor
    implements DETemporalsVisitor2 {

  @Override
  public void endVisit(ASTDEAlphanumericDate lit) {
    setTypeOfInstantLiteral(lit, lit);
  }

  @Override
  public void endVisit(ASTDETime lit) {
    setTypeOfInstantLiteral(lit, lit);
  }

  @Override
  public void endVisit(ASTDEDateTime lit) {
    setTypeOfInstantLiteral(lit, lit);
  }

}
