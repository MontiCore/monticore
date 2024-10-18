// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

/**
 * @deprecated use {@link WithinScopeBasicSymbolsResolver}
 */
@Deprecated
public class NameExpressionTypeCalculator extends WithinScopeBasicSymbolsResolver {

  protected NameExpressionTypeCalculator(
      TypeContextCalculator typeCtxCalc,
      WithinTypeBasicSymbolsResolver withinTypeResolver
  ) {
    super(typeCtxCalc, withinTypeResolver);
  }

  public NameExpressionTypeCalculator() {
    super();
  }

}
