/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.escapedtemporalliterals.types3;

import de.monticore.temporal.escapedtemporalliterals._ast.ASTEscapedTemporalLiteral;
import de.monticore.temporal.escapedtemporalliterals._visitor.EscapedTemporalLiteralsVisitor2;
import de.monticore.temporal.temporalbasis.TemporalBasisMill;
import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._ast.ASTDateTime;
import de.monticore.temporal.temporalbasis._ast.ASTInstant;
import de.monticore.temporal.temporalbasis._ast.ASTTime;
import de.monticore.temporal.temporalbasis._visitor.TemporalBasisTraverser;
import de.monticore.temporal.temporalbasis._visitor.TemporalBasisVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.temporal.TemporalSymTypeFactory;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from EscapedTemporalLiterals
 */
public class EscapedTemporalLiteralsTypeVisitor extends AbstractTypeVisitor
    implements EscapedTemporalLiteralsVisitor2 {

  @Override
  public void endVisit(ASTEscapedTemporalLiteral lit) {
    SymTypeExpression type;
    if (lit.isPresentInstant()) {
      type = calculateTypeOfInstantOrLogError(lit.getInstant());
    }
    else {
      type = TemporalSymTypeFactory.createPeriod();
    }
    getType4Ast().setTypeOfExpression(lit, type);
  }

  protected SymTypeExpression calculateTypeOfInstantOrLogError(ASTInstant instant) {
    Optional<SymTypeExpression> type = InstantTypeCalculator.calculateTypeOfInstant(instant);
    if (type.isEmpty()) {
      Log.error("0xFD3A1 internal error: unexpected kind of Instant.",
          instant.get_SourcePositionStart(),
          instant.get_SourcePositionEnd()
      );
      return SymTypeExpressionFactory.createObscureType();
    }
    return type.get();
  }

  /**
   * Calculates the type of a single Instant,
   * i.e., TimePoint for Dates and DateTimes, and DayTime for Times.
   * <p>
   * Note: This may need to be extended in the future.
   * Alternative: getType()-method in ASTInstant that simply returns the type
   */
  protected static class InstantTypeCalculator
      implements TemporalBasisVisitor2 {

    protected Optional<SymTypeExpression> type = Optional.empty();

    public static Optional<SymTypeExpression> calculateTypeOfInstant(ASTInstant instant) {
      InstantTypeCalculator calculator = new InstantTypeCalculator();
      TemporalBasisTraverser traverser =
          TemporalBasisMill.singleStepTraverser();
      traverser.add4TemporalBasis(calculator);
      instant.accept(traverser);
      return calculator.type;
    }

    @Override
    public void visit(ASTDate node) {
      type = Optional.of(TemporalSymTypeFactory.createTimePoint());
    }

    @Override
    public void visit(ASTDateTime node) {
      type = Optional.of(TemporalSymTypeFactory.createTimePoint());
    }

    @Override
    public void visit(ASTTime node) {
      type = Optional.of(TemporalSymTypeFactory.createDayTime());
    }

  }

}
