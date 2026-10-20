/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.escapedtemporalliterals.types3;

import de.monticore.temporal.escapedtemporalliterals._ast.ASTEscapedTemporalLiteral;
import de.monticore.temporal.escapedtemporalliterals._visitor.EscapedTemporalLiteralsVisitor2;
import de.monticore.temporal.temporalbasis.types3.TemporalBasisTypeVisitor;
import de.monticore.types.check.SymTypeExpression;

/**
 * Derives the temporal type of escaped temporal literals,
 * e.g., {@code d"2015-04-01"} is a TimePoint
 * and {@code d"P1D"} is a Period.
 */
public class EscapedTemporalLiteralsTypeVisitor extends TemporalBasisTypeVisitor
    implements EscapedTemporalLiteralsVisitor2 {

  @Override
  public void endVisit(ASTEscapedTemporalLiteral lit) {
    SymTypeExpression type;
    if (lit.isPresentInstant()) {
      type = typeOfInstant(lit.getInstant());
    }
    else {
      type = typeOfPeriod(lit.getPeriod());
    }
    getType4Ast().setTypeOfExpression(lit, type);
  }

}
