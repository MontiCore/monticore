/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types2.ISymTypeVisitor;

/**
 * An objectType is a full qualified class name.
 * Therefore, we have the fullName, the baseName and the
 * Symbol behind that full qualified class name to retrieve
 */
public class SymTypeOfObject extends SymTypeExpression {

  protected TypeSymbol typeSymbol;

  /**
   * Constructor: with a TypeSymbolSurrogate that contains the name and enclosingScope
   */
  public SymTypeOfObject(TypeSymbol typeSymbol)
  {
    this.typeSymbol = typeSymbol;
  }

  public TypeSymbol getTypeInfo() {
    return typeSymbol;
  }

  /**
   * @deprecated questionable name: getter and setter do different things.
   * one may add a getObjFullName() or similar if required
   * also, seems unused in our main projects
   */
  @Deprecated
  public String getObjName() {
    return typeSymbol.getFullName();
  }

  /**
   * @deprecated unused in main projects
   */
  @Deprecated
  public void setObjName(String objname) {
    this.typeSymbol.setName(objname);
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String printFullName() {
    return typeSymbol.getFullName();
  }

  @Override
  public String print(){
    return typeSymbol.getName();
  }

  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   * @deprecated unused outside of tests, but not required for tests
   */
  @Deprecated
  public String getBaseName() {
    String[] parts = getObjName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isObjectType() {
    return true;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!sym.isObjectType()){
      return false;
    }
    SymTypeOfObject symCon = (SymTypeOfObject) sym;
    if(this.typeSymbol == null ||symCon.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symCon.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symCon.typeSymbol.getName())){
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
