/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SymTypeVariable extends SymTypeExpression {

  protected static final String LOG_NAME = "SymTypeVariable";

  protected TypeVarSymbol typeVarSymbol;

  public SymTypeVariable(TypeVarSymbol typeSymbol) {
    this.typeVarSymbol = Log.errorIfNull(typeSymbol);
  }

  @Deprecated
  public SymTypeVariable(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
    if (typeSymbol instanceof TypeVarSymbol) {
      this.typeVarSymbol = (TypeVarSymbol) typeSymbol;
    }
  }

  /**
   * @deprecated (should) return true
   */
  @Deprecated
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
  @Deprecated
  public TypeSymbol getTypeInfo() {
    Log.debug("0xFDFDC internal error: getTypeInfo called"
            + ", but the current SymTypeExpression should never have"
            + " a TypeSymbol in the first place."
            + " (This will be an error in the future)",
        LOG_NAME
    );
    //support deprecated behavior
    if (typeSymbol != null) {
      return typeSymbol;
    }
    return getTypeVarSymbol();
  }

  /**
   * a type variable only allows sub-types of its upper bound,
   * e.g., T extends Number
   */
  public SymTypeExpression getUpperBound() {
    return SymTypeExpressionFactory.createIntersectionOrDefault(
        SymTypeExpressionFactory.createTopType(),
        getTypeVarSymbol().getSuperTypesList()
    );
  }

  /**
   * @deprecated unused in main projects
   *     also: getter and setter do something different, questionable
   */
  @Deprecated(forRemoval = true)
  public String getVarName() {
    return getTypeInfo().getFullName();
  }

  /**
   * @deprecated unused in main projects
   */
  @Deprecated(forRemoval = true)
  public void setVarName(String name) {
    typeSymbol.setName(name);
  }

  @Override
  public String print() {
    //support deprecated code:
    if (typeSymbol != null) {
      return typeSymbol.getName();
    }
    return super.print();
  }

  @Override
  public String printFullName() {
    //support deprecated code:
    if (typeSymbol != null) {
      return getVarName();
    }
    return super.printFullName();
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
  public SymTypeVariable asTypeVariable() {
    return this;
  }

  @Override
  public SymTypeVariable deepClone() {
    //support deprecated code:
    if (typeSymbol != null) {
      return new SymTypeVariable(this.typeSymbol);
    }
    return new SymTypeVariable(getTypeVarSymbol());
  }

  /**
   * Similar to deepEquals, but only checks
   * whether the variables are supposed to be the same variable.
   * E.g., bounds are ignored
   */
  public boolean denotesSameVar(SymTypeExpression other) {
    if (!other.isTypeVariable()) {
      return false;
    }
    SymTypeVariable otherVar = other.asTypeVariable();
    if (hasTypeVarSymbol() && otherVar.hasTypeVarSymbol()) {
      if (!getTypeVarSymbol().getEnclosingScope()
          .equals(otherVar.getTypeVarSymbol().getEnclosingScope())
      ) {
        return false;
      }
      if (!getTypeVarSymbol().getName().equals(
          otherVar.getTypeVarSymbol().getName())
      ) {
        return false;
      }
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    //support deprecated code:
    if (typeSymbol != null) {
      if (!(sym instanceof SymTypeVariable)) {
        return false;
      }
      SymTypeVariable symVar = (SymTypeVariable) sym;
      if (this.typeSymbol == null || symVar.typeSymbol == null) {
        return false;
      }
      if (!this.typeSymbol.getEnclosingScope().equals(symVar.typeSymbol.getEnclosingScope())) {
        return false;
      }
      if (!this.typeSymbol.getName().equals(symVar.typeSymbol.getName())) {
        return false;
      }
      return this.print().equals(symVar.print());
    }
    if (!sym.isTypeVariable()) {
      return false;
    }
    if (sym == this) {
      return true;
    }
    SymTypeVariable symVar = (SymTypeVariable) sym;
    if (!denotesSameVar(symVar)) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

}
