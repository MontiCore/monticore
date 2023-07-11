/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class SymTypeVariable extends SymTypeExpression {

  protected static final String FREE_VARIABLE_NAME = "__INTERNAL_TYPEVARIABLE";

  /**
   * may be null, as some type variables are created
   * DURING the type checking process and thus have no symbols
   */
  protected TypeVarSymbol typeVarSymbol;

  protected SymTypeExpression lowerBound;

  /**
   * this is NOT the full upper bound,
   * given a TypeVarSymbol, it's supertypes are added to this upperBound
   */
  protected SymTypeExpression upperBound;

  /**
   * @param typeVarSymbol is allowed to be null
   */
  public SymTypeVariable(
      TypeVarSymbol typeVarSymbol,
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    this.typeVarSymbol = typeVarSymbol;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Deprecated
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

  public boolean hasTypeVarSymbol() {
    return typeVarSymbol != null;
  }

  public TypeVarSymbol getTypeVarSymbol() {
    if (hasTypeVarSymbol()) {
      return typeVarSymbol;
    }
    Log.error("0xFDFDD internal error: getTypeVarSymbol called, "
        + "but no TypeVarSymbol available");
    return null;
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
   * a type variable only allows super types of its lower bound
   */
  protected SymTypeExpression getStoredLowerBound() {
    return lowerBound;
  }

  public SymTypeExpression getLowerBound() {
    return getStoredLowerBound();
  }

  protected SymTypeExpression getStoredUpperBound() {
    return upperBound;
  }

  /**
   * a type variable only allows sub-types of its upper bound,
   * e.g., T extends Number
   */
  public SymTypeExpression getUpperBound() {
    // add upper bound given by symbol if applicable
    SymTypeExpression result;
    if (hasTypeVarSymbol() && !getTypeVarSymbol().isEmptySuperTypes()) {
      Set<SymTypeExpression> intersectedTypes =
          new HashSet<>(getTypeVarSymbol().getSuperTypesList());
      intersectedTypes.add(getStoredUpperBound());
      result = SymTypeExpressionFactory.createIntersection(intersectedTypes);
    }
    else {
      result = getStoredUpperBound();
    }
    return result;
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
    if (!hasTypeVarSymbol()) {
      return FREE_VARIABLE_NAME;
    }
    return getTypeVarSymbol().getName();
  }

  @Override
  public String printFullName() {
    //support deprecated code:
    if(typeSymbol != null) {
    return getVarName();
    }
    if (!hasTypeVarSymbol()) {
      return FREE_VARIABLE_NAME;
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
    if (hasTypeVarSymbol()) {
      return new SymTypeVariable(
          getTypeVarSymbol(),
          getLowerBound(),
          getUpperBound()
      );
    }
    else {
      return new SymTypeVariable(null, getLowerBound(), getUpperBound());
    }
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
    if(sym == this) {
      return true;
    }
    // cannot identify without a name at this point
    else if(!hasTypeVarSymbol()) {
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
