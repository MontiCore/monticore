// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * replaces TypeVariables using a given map
 * e.g., T, {T->int,U->float} -> int
 * e.g., List<T>, {T->int} -> List<int>
 * Usage:
 * calculate(symType, replaceMap)
 */
public class SymTypeVariableReplaceVisitor extends SymTypeDeepCloneVisitor {

  /**
   * Map for replacing Type Variables
   */
  protected Map<TypeVarSymbol, SymTypeExpression> replaceMap = new HashMap<>();

  public Map<TypeVarSymbol, SymTypeExpression> getReplaceMap() {
    return replaceMap;
  }

  public void setReplaceMap(Map<TypeVarSymbol, SymTypeExpression> replaceMap) {
    this.replaceMap = replaceMap;
  }

  @Override
  public void visit(SymTypeVariable typVar) {
    // as containsKey uses equals, we need to go other it ourselves
    boolean inMap = false;
    for (TypeVarSymbol varSym : getReplaceMap().keySet()) {
      if (varSym.deepEquals(typVar.getTypeVarSymbol()) && !inMap) {
        pushTransformedSymType(getReplaceMap().get(varSym));
        inMap = true;
      }
    }
    if (!inMap) {
      pushTransformedSymType(typVar);
    }
  }

  // Helpers

  public SymTypeExpression calculate(
      SymTypeExpression symType,
      Map<TypeVarSymbol, SymTypeExpression> replaceMap
  ) {
    Map<TypeVarSymbol, SymTypeExpression> oldMap = this.replaceMap;
    setReplaceMap(replaceMap);
    SymTypeExpression result = calculate(symType);
    setReplaceMap(oldMap);
    return result;
  }
}
