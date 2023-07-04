/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;

public class SymTypeVariable extends SymTypeExpression {

  protected TypeVarSymbol typeVarSymbol;

  /**
   * Constructor:
   */
  public SymTypeVariable(TypeVarSymbol typeSymbol) {
    this.typeVarSymbol = typeSymbol;
  }

  @Deprecated
  public SymTypeVariable(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
    if(typeSymbol instanceof TypeVarSymbol) {
      this.typeVarSymbol = (TypeVarSymbol) typeSymbol;
    }
  }

  public TypeVarSymbol getTypeVarSymbol() {
    return typeVarSymbol;
  }

  @Override
  public boolean hasTypeInfo() {
    // support deprecated behavior
    return typeVarSymbol != null || typeSymbol != null;
  }

  @Override
  @Deprecated
  public TypeSymbol getTypeInfo() {
    //support deprecated behavior
    if(typeSymbol != null) {
      return typeSymbol;
    }
    return getTypeVarSymbol();
  }

  /**
   * @deprecated unused in main projects
   * also: getter and setter do something different, questionable
   */
  @Deprecated
  public String getVarName() {
    return getTypeInfo().getFullName();
  }

  /**
   * @deprecated unused in main projects
   */
  @Deprecated
  public void setVarName(String name) {
    typeSymbol.setName(name);
  }

  @Override
  public String print() {
    //support deprecated code:
    if(typeSymbol != null) {
    return typeSymbol.getName();
    }
    return getTypeVarSymbol().getName();
  }

  @Override
  public String printFullName() {
    //support deprecated code:
    if(typeSymbol != null) {
    return getVarName();
    }
    return getTypeVarSymbol().getFullName();
  }

  public boolean isPrimitive() {
    return false;
    /**
     *     Please note that the var itself is not a primitive type, but it might
     *     be instantiated into a primitive type
     *     unless we always assume boxed implementations then return false would be correct
     *     according to the W algorithm of Hindley-Milner, we regard a variable
     *     a monomorphic type on its own and do hence not regard it as primitive type
     */
  }

  @Override
  public boolean isValidType() {
    return false;
    /**
     *     Please note that the var itself is not a type,
     *     but it might be instantiated into a type
     */
  }

  @Override
  public boolean isTypeVariable() {
    return true;
  }

  @Override
  public SymTypeVariable deepClone() {
    //support deprecated code:
    if(typeSymbol != null) {
    return new SymTypeVariable(this.typeSymbol);
    }
    return new SymTypeVariable(getTypeVarSymbol());
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    //support deprecated code:
    if(typeSymbol != null) {
    if(!(sym instanceof SymTypeVariable)){
      return false;
    }
    SymTypeVariable symVar = (SymTypeVariable) sym;
    if(this.typeSymbol == null ||symVar.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symVar.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symVar.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symVar.print());
    }
    if (!sym.isTypeVariable()) {
      return false;
    }
    SymTypeVariable symVar = (SymTypeVariable) sym;
    if (!getTypeVarSymbol().getFullName().equals(
        symVar.getTypeVarSymbol().getFullName())) {
      return false;
    }
    if (!getTypeVarSymbol().getEnclosingScope().equals(
        symVar.getTypeVarSymbol().getEnclosingScope())) {
      return false;
    }
    if (!getTypeVarSymbol().getSpannedScope().equals(
        symVar.getTypeVarSymbol().getSpannedScope())) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

}
