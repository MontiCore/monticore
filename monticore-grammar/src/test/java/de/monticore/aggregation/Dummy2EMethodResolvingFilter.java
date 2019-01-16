package de.monticore.aggregation;

import de.monticore.aggregation.blah._symboltable.DummyKind;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

public class Dummy2EMethodResolvingFilter extends TransitiveAdaptedResolvingFilter<EMethodSymbol> {

  public Dummy2EMethodResolvingFilter() {
    super(DummyKind.KIND,EMethodSymbol.class, EMethodSymbol.KIND);
  }

  @Override
  public Symbol translate(Symbol s) {
    return new Dummy2MethodAdapterSymbol((DummySymbol) s);
  }

}
