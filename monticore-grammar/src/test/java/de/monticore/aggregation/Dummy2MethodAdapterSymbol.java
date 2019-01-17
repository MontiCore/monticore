package de.monticore.aggregation;

import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.SourcePosition;

public class Dummy2MethodAdapterSymbol extends EMethodSymbol implements SymbolAdapter<DummySymbol> {

  private DummySymbol adaptee;

  public Dummy2MethodAdapterSymbol(DummySymbol dummySymbol) {
    super(dummySymbol.getName());
    this.adaptee = dummySymbol;
  }

  @Override
  public DummySymbol getAdaptee() {
    return adaptee;
  }

//  @Override
//  public ASTMCType getReturnType() {
//    this.adaptee.getAstNode()
//  }


}
