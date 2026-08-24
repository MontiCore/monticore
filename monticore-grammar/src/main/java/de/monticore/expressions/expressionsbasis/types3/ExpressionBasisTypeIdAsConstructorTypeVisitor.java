/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;

/**
 * s. {@link de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorTypeVisitor}
 *
 * @deprecated use {@link ExpressionBasisTypeIdAsConstructorCTTIVisitor}
 */
@Deprecated(forRemoval = true)
public class ExpressionBasisTypeIdAsConstructorTypeVisitor
    extends ExpressionBasisTypeVisitor {

  /**
   * @deprecated is now a static delegate
   */
  @Deprecated(forRemoval = true)
  public void setOOWithinTypeResolver(
      OOWithinTypeBasicSymbolsResolver oOWithinTypeResolver) {
  }

}