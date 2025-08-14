package de.monticore.interpreter.values;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;

public class VariableMIValue extends WriteableMIValue {

  protected MIScope scope;
  protected VariableSymbol symbol;

  protected Optional<MIValue> innerValue = Optional.empty();

  public VariableMIValue(MIScope scope, VariableSymbol symbol) {
    this.scope = scope;
    this.symbol = symbol;
  }

  @Override
  public void write(MIValue value) {
    scope.storeVariable(symbol, value);
    innerValue = Optional.of(value);
  }

  @Override
  public MIValue getMIValue() {
    if (!innerValue.isPresent()) {
      innerValue = Optional.of(scope.loadVariable(symbol));
    }

    return innerValue.get();
  }

  @Override
  public String printType() {
    return "Variable";
  }

  @Override
  public String printValue() {
    return getMIValue().printType() + " (" + getMIValue().printValue() + ")";
  }
}
