// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.util.SymTypeDeepCloneVisitor;
import de.monticore.types3.util.SymTypeExpressionComparator;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

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
  protected Map<SymTypeVariable, SymTypeExpression> replaceMap =
      Collections.emptyMap();

  public Map<SymTypeVariable, SymTypeExpression> getReplaceMap() {
    return replaceMap;
  }

  public void setReplaceMap(Map<SymTypeVariable, SymTypeExpression> replaceMap) {
    this.replaceMap = replaceMap;
  }

  @Override
  public void visit(SymTypeVariable typVar) {
    // as containsKey uses equals, we need to go other it ourselves
    boolean inMap = false;
    for (SymTypeVariable keyTypeVar : getReplaceMap().keySet()) {
      if (typVar.denotesSameVar(keyTypeVar) & !inMap) {
        pushTransformedSymType(getReplaceMap().get(keyTypeVar));
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
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  ) {
    Map<SymTypeVariable, SymTypeExpression> oldMap = this.replaceMap;
    // assure that the map used does not rely on hashes
    Map<SymTypeVariable, SymTypeExpression> newMap =
        new TreeMap<>(new SymTypeExpressionComparator());
    newMap.putAll(replaceMap);
    setReplaceMap(newMap);
    SymTypeExpression result = calculate(symType);
    setReplaceMap(oldMap);
    return result;
  }

}
