/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.types3;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._ast.ASTDateTime;
import de.monticore.temporal.temporalbasis._ast.ASTInstant;
import de.monticore.temporal.temporalbasis._ast.ASTPeriod;
import de.monticore.temporal.temporalbasis._ast.ASTTime;
import de.monticore.temporal.types3.TemporalSymTypeFactory;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

/**
 * Base class of the type visitors of the temporal languages.
 * Maps the interfaces of TemporalBasis to the temporal types:
 * Date and DateTime to TimePoint, Time to DayTime, and Period to Period.
 * <p>
 * As the mapping only relies on these interfaces,
 * new temporal languages (e.g., for further locales) only need a visitor
 * for their productions that implement Literal.
 */
public abstract class TemporalBasisTypeVisitor extends AbstractTypeVisitor {

  protected SymTypeExpression typeOfInstant(ASTInstant instant) {
    if (instant instanceof ASTDate || instant instanceof ASTDateTime) {
      return TemporalSymTypeFactory.createTimePoint();
    }
    else if (instant instanceof ASTTime) {
      return TemporalSymTypeFactory.createDayTime();
    }
    Log.error("0xFDE02 internal error: unknown kind of Instant, "
            + "expected a Date, Time, or DateTime.",
        instant.get_SourcePositionStart(),
        instant.get_SourcePositionEnd()
    );
    return createObscureType();
  }

  protected SymTypeExpression typeOfPeriod(ASTPeriod period) {
    return TemporalSymTypeFactory.createPeriod();
  }

  protected void setTypeOfInstantLiteral(ASTLiteral lit, ASTInstant instant) {
    getType4Ast().setTypeOfExpression(lit, typeOfInstant(instant));
  }

}
