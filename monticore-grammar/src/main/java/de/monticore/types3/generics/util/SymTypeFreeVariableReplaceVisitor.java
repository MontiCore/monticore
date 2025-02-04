// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.util.SymTypeDeepCloneVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaces type variables, that are free in the given enclosing scope,
 * with inference variables.
 * Returns both, the new type,
 * and a map from the replaced variables to the replacement.
 * Usage:
 * calculate(symType, enclosingScope)
 */
public class SymTypeFreeVariableReplaceVisitor extends SymTypeDeepCloneVisitor {

  /**
   * Map for replacing Type Variables
   */
  protected Map<SymTypeVariable, SymTypeInferenceVariable> replaceMap;

  protected IBasicSymbolsScope enclosingScope;

  protected Map<SymTypeVariable, SymTypeInferenceVariable> getReplaceMap() {
    return replaceMap;
  }

  public void setReplaceMap(Map<SymTypeVariable, SymTypeInferenceVariable> replaceMap) {
    this.replaceMap = replaceMap;
  }

  protected IBasicSymbolsScope getEnclosingScope() {
    return enclosingScope;
  }

  public void setEnclosingScope(IBasicSymbolsScope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }

  @Override
  public void visit(SymTypeVariable typeVar) {
    // only replace free vars
    boolean isFree = false;
    if (typeVar.hasTypeVarSymbol()) {
      TypeVarSymbol sym = typeVar.getTypeVarSymbol();
      isFree = !getEnclosingScope().isTypeVariableBound(sym);
    }

    if (!isFree) {
      pushTransformedSymType(typeVar);
    }
    else {
      // as containsKey uses equals, we need to go other it ourselves
      SymTypeInferenceVariable replacement = null;
      for (SymTypeVariable keyTypeVar : getReplaceMap().keySet()) {
        if (typeVar.denotesSameVar(keyTypeVar) && replacement == null) {
          replacement = getReplaceMap().get(keyTypeVar);
        }
      }
      if (replacement == null) {
        replacement = SymTypeExpressionFactory.createInferenceVariable(
            SymTypeExpressionFactory.createBottomType(),
            typeVar.getUpperBound(),
            // better naming, reference the replaced type
            "FV{" + typeVar.printFullName() + "}"
        );
        getReplaceMap().put(typeVar, replacement);
      }
      pushTransformedSymType(replacement);
    }
  }

  // Helpers

  public Result calculate(
      SymTypeExpression symType,
      IBasicSymbolsScope enclosingScope
  ) {
    Map<SymTypeVariable, SymTypeInferenceVariable> oldMap = this.replaceMap;
    setReplaceMap(new HashMap<>());
    IBasicSymbolsScope oldScope = this.enclosingScope;
    setEnclosingScope(enclosingScope);

    SymTypeExpression resultType = calculate(symType);
    Result result =
        new Result(resultType, getReplaceMap());

    setEnclosingScope(oldScope);
    setReplaceMap(oldMap);
    return result;
  }

  /**
   * just a pair
   */
  public static class Result {

    final protected SymTypeExpression type;
    final protected Map<SymTypeVariable, SymTypeInferenceVariable> replaceMap;

    public Result(
        SymTypeExpression type,
        Map<SymTypeVariable, SymTypeInferenceVariable> replaceMap
    ) {
      this.type = type;
      this.replaceMap = replaceMap;
    }

    public SymTypeExpression getType() {
      return type;
    }

    public Map<SymTypeVariable, SymTypeInferenceVariable> getReplaceMap() {
      return replaceMap;
    }

  }

}
