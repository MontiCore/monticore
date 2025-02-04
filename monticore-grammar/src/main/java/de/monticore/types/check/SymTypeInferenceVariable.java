/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

public class SymTypeInferenceVariable extends SymTypeExpression {

  /**
   * This holds a number of the free type variable,
   * which acts as an identifier.
   * The Only guarantee is that the ids are unique.
   * Never test against them.
   */
  protected int id;

  /**
   * a short string, e.g., "FV", that is output with the id while printing
   */
  protected String idStr;

  protected SymTypeExpression lowerBound;

  protected SymTypeExpression upperBound;

  public SymTypeInferenceVariable(
      String idStr,
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    this.id = getUniqueID();
    this.idStr = Log.errorIfNull(idStr);
    this.lowerBound = Log.errorIfNull(lowerBound);
    this.upperBound = Log.errorIfNull(upperBound);
  }

  /**
   * INTERNAL, DO NOT USE: used for deepcloning
   */
  public SymTypeInferenceVariable(
      int id,
      String idStr,
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    this.id = id;
    this.idStr = Log.errorIfNull(idStr);
    this.lowerBound = Log.errorIfNull(lowerBound);
    this.upperBound = Log.errorIfNull(upperBound);
  }

  public SymTypeExpression getLowerBound() {
    return lowerBound;
  }

  public SymTypeExpression getUpperBound() {
    return upperBound;
  }

  /**
   * accessible for printing, sorting, etc.
   * usually, you do not need/want this.
   */
  public int _internal_getID() {
    return id;
  }

  /**
   * accessible for printing
   * usually, you do not need/want this.
   */
  public String _internal_getIDStr() {
    return idStr;
  }

  public boolean isPrimitive() {
    /*
     * Please note that the var itself is not a primitive type,
     * but it might be instantiated into a primitive type
     * unless we always assume boxed implementations
     * then return false would be correct
     * according to the W algorithm of Hindley-Milner,
     * we regard a variable a monomorphic type on its own and
     * do hence not regard it as primitive type
     */
    return false;
  }

  @Override
  public boolean isInferenceVariable() {
    return true;
  }

  @Override
  public SymTypeInferenceVariable asInferenceVariable() {
    return this;
  }

  @Override
  public SymTypeInferenceVariable deepClone() {
    return super.deepClone().asInferenceVariable();
  }

  /**
   * Similar to deepEquals, but only checks
   * whether the variables are supposed to be the same variable.
   * E.g., bounds are ignored
   */
  public boolean denotesSameVar(SymTypeExpression other) {
    if (!other.isInferenceVariable()) {
      return false;
    }
    SymTypeInferenceVariable otherVar = other.asInferenceVariable();
    return id == otherVar.id;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (sym == this) {
      return true;
    }
    if (!sym.isInferenceVariable()) {
      return false;
    }
    SymTypeInferenceVariable symVar = sym.asInferenceVariable();
    if (!denotesSameVar(symVar)) {
      return false;
    }
    if (!getUpperBound().deepEquals(symVar.getUpperBound())) {
      return false;
    }
    else if (!getLowerBound().deepEquals(symVar.getLowerBound())) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

  // id management

  protected static int getUniqueID() {
    // naming inspired by JDK
    // s.a. https://git.rwth-aachen.de/monticore/monticore/-/issues/4296
    return typeInfIDCounter++;
  }

  protected static int typeInfIDCounter = 0;
}
