// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

/**
 * normalizes SymTypes
 * delegate of SymTypeRelations
 */
public class SymTypeNormalizer extends SymTypeRelations {

  protected SymTypeNormalizeVisitor normalizeVisitor = null;

  @Override
  protected void setSymTypeRelations(SymTypeRelations realThis) {
    super.setSymTypeRelations(realThis);
    normalizeVisitor = new SymTypeNormalizeVisitor(realThis);
  }

  @Override
  protected SymTypeExpression normalize(SymTypeExpression type) {
    if (normalizeVisitor == null) {
      Log.error("0xFDF11 internal error: "
          + "SymTypeNormalizer was not initialized.");
      return SymTypeExpressionFactory.createObscureType();
    }
    return normalizeVisitor.calculate(type);
  }

}
