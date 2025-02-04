// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types3.util.SymTypeDeepCloneVisitor;
import de.monticore.types3.util.SymTypeExpressionComparator;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * replaces InferenceVariables using a given map
 * e.g., a, {a->int,b->float} -> int
 * e.g., List<a>, {a->int} -> List<int>
 * Usage:
 * calculate(symType, replaceMap)
 */
public class SymTypeInferenceVariableReplaceVisitor extends SymTypeDeepCloneVisitor {

  /**
   * Map for replacing Type Variables
   */
  protected Map<SymTypeInferenceVariable, SymTypeExpression> replaceMap =
      Collections.emptyMap();

  public Map<SymTypeInferenceVariable, SymTypeExpression> getReplaceMap() {
    return replaceMap;
  }

  public void setReplaceMap(Map<SymTypeInferenceVariable, SymTypeExpression> replaceMap) {
    this.replaceMap = replaceMap;
  }

  @Override
  public void visit(SymTypeInferenceVariable typVar) {
    // as containsKey uses equals, we need to go other it ourselves
    boolean inMap = false;
    for (SymTypeInferenceVariable keyTypeVar : getReplaceMap().keySet()) {
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
      Map<SymTypeInferenceVariable, ? extends SymTypeExpression> replaceMap
  ) {
    Map<SymTypeInferenceVariable, SymTypeExpression> oldMap = this.replaceMap;
    // assure that the map used does not rely on hashes
    Map<SymTypeInferenceVariable, SymTypeExpression> newMap =
        new TreeMap<>(new SymTypeExpressionComparator());
    newMap.putAll(replaceMap);
    setReplaceMap(newMap);
    SymTypeExpression result = calculate(symType);
    setReplaceMap(oldMap);
    return result;
  }

}
