/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;


import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types3.ISymTypeVisitor;

public class SymTypeVoid extends SymTypeExpression {

  /**
   * @deprecated no logic that is not deprecated
   */
  @Deprecated
  public SymTypeVoid() {
    typeSymbol = new TypeSymbolSurrogate(BasicSymbolsMill.VOID);
    typeSymbol.setEnclosingScope(BasicSymbolsMill.scope());
  }
  
  @Override
  public String print() {
    return BasicSymbolsMill.VOID;
  }

  @Override
  public String printFullName() {
    return print();
  }

  @Override
  public boolean isVoidType() {
    return true;
  }


  @Override
  public SymTypeVoid asVoidType(){
    return this;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    return sym.isVoidType();
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

}
