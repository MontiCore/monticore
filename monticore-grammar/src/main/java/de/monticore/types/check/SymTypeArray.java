/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Arrays of a certain dimension (>= 1)
 */
public class SymTypeArray extends SymTypeExpression {

  /**
   * An arrayType has a dimension (>= 1)
   */
  protected int dim;

  /**
   * An Array has an argument Type
   */
  protected SymTypeExpression argument;

  /**
   * Constructor
   *
   * @deprecated TypeSymbul UND Expression: da ist was doppelt?
   *
   * @param dim      dimension
   * @param argument Argument Type
   * @param typeSymbol loader for the Type-Symbol that defines this type
   */
  @Deprecated
  public SymTypeArray(TypeSymbol typeSymbol, int dim, SymTypeExpression argument) {
    this.typeSymbol = typeSymbol;
    this.dim = dim;
    this.argument = argument;
  }

  public SymTypeArray(SymTypeExpression argument, int dim) {
    this.dim = dim;
    this.argument = argument;
  }

  // ------------------------------------------------------------------ Functions

  @Override
  public boolean isArrayType() {
    return true;
  }

  @Override
  public SymTypeArray asArrayType() {
    return this;
  }


  public int getDim() {
    return dim;
  }

  public void setDim(int dim) {
    this.dim = dim;
  }

  /**
   * returns a clone, but with n dimensions less
   */
  public SymTypeExpression cloneWithLessDim(int n) {
    if (n > getDim()) {
      Log.error("0xFDCCE tried removing " + n
          + "dimensions from an " + getDim() + "dimensional array");
      return SymTypeExpressionFactory.createObscureType();
    }
    else if (n == getDim()) {
      return getArgument().deepClone();
    }
    else {
      SymTypeArray clone = this.deepClone();
      clone.setDim(getDim() - n);
      return clone;
    }
  }

  public SymTypeExpression getArgument() {
    return argument;
  }

  public void setArgument(SymTypeExpression argument) {
    this.argument = argument;
  }

  @Override
  public SymTypeArray deepClone() {
    //to support deprecated code:
    if(typeSymbol != null) {
      TypeSymbol typeSymbol = new TypeSymbolSurrogate(this.typeSymbol.getName());
      typeSymbol.setEnclosingScope(this.typeSymbol.getEnclosingScope());
      return new SymTypeArray(typeSymbol,
          this.dim, this.argument.deepClone());
    }
    else {
      return new SymTypeArray(getArgument().deepClone(), getDim());
    }
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    //to support deprecated code:
    if(typeSymbol != null) {
      if (!(sym instanceof SymTypeArray)) {
        return false;
      }
      SymTypeArray symArr = (SymTypeArray) sym;
      if(this.dim!=symArr.dim){
        return false;
      }
      if(this.typeSymbol == null ||symArr.typeSymbol ==null){
        return false;
      }
      if(!this.typeSymbol.getEnclosingScope().equals(symArr.typeSymbol.getEnclosingScope())){
        return false;
      }
      if(!this.typeSymbol.getName().equals(symArr.typeSymbol.getName())){
        return false;
      }
      if(!this.getArgument().deepEquals(symArr.getArgument())){
        return false;
      }
      return this.print().equals(symArr.print());
    }
    if (!sym.isArrayType()) {
      return false;
    }
    SymTypeArray symArr = (SymTypeArray) sym;
    if (getDim() != symArr.getDim()) {
      return false;
    }
    if (!getArgument().deepEquals(symArr.getArgument())) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
