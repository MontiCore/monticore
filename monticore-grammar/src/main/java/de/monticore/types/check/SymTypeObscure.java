/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.ISymTypeVisitor;

import java.util.Collections;
import java.util.List;

public class SymTypeObscure extends SymTypeExpression {

  @Override
  public SymTypeExpression deepClone() {
    return new SymTypeObscure();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    return sym.isObscureType();
  }

  @Override
  public boolean isValidType() {
    return false;
  }

  @Override
  public boolean isObscureType() {
    return true;
  }

  @Override
  public SymTypeObscure asObscureType() {
    return this;
  }

  @Override
  public List<FunctionSymbol> getMethodList(String methodname,
                                            boolean abstractTc,
                                            AccessModifier modifier) {
    return Collections.emptyList();
  }

  @Override
  protected List<FunctionSymbol> getCorrectMethods(String methodName,
                                                   boolean outerIsType,
                                                   boolean abstractTc,
                                                   AccessModifier modifier) {
    return Collections.emptyList();
  }

  @Override
  public List<VariableSymbol> getFieldList(String fieldName,
                                           boolean abstractTc,
                                           AccessModifier modifier) {
    return Collections.emptyList();
  }

  @Override
  public List<VariableSymbol> getFieldList(String fieldName,
                                           boolean outerIsType,
                                           boolean abstractTc,
                                           AccessModifier modifier) {
    return Collections.emptyList();
  }

  @Override
  protected List<VariableSymbol> getCorrectFields(String fieldName,
                                                  boolean outerIsType,
                                                  boolean abstractTc,
                                                  AccessModifier modifier) {
    return Collections.emptyList();
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
