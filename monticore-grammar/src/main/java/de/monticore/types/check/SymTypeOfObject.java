/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

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
    Preconditions.checkNotNull(typeSymbol);
    this.typeSymbol = typeSymbol;
  }

  @Override
  public boolean hasTypeInfo() {
    // should allways be true
    return typeSymbol != null;
  }

  @Override
  public TypeSymbol getTypeInfo() {
    return typeSymbol;
  }

  /**
   * @deprecated questionable name: getter and setter do different things.
   * one may add a getObjFullName() or similar if required
   * also, seems unused in our main projects
   */
  @Deprecated(forRemoval = true)
  public String getObjName() {
    return typeSymbol.getFullName();
  }

  /**
   * @deprecated unused in main projects
   */
  @Deprecated(forRemoval = true)
  public void setObjName(String objname) {
    this.typeSymbol.setName(objname);
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   * @deprecated unused outside of tests, but not required for tests
   * use {@link de.se_rwth.commons.Names} instead,
   * or {@code getTypeInfo().getName()}
   */
  @Deprecated(forRemoval = true)
  public String getBaseName() {
    String[] parts = getObjName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isObjectType() {
    return true;
  }

  @Override
  public SymTypeOfObject asObjectType(){return this;}

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
