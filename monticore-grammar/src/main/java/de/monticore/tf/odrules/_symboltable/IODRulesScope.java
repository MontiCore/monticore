/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IODRulesScope extends IODRulesScopeTOP {


  /*
   * Ensure we are able to resolve properly in anonymous scopes
   */
  default List<ODDefinitionSymbol> continueAsODDefinitionSubScope(
          boolean foundSymbols, String name,
          AccessModifier modifier,
          Predicate<ODDefinitionSymbol> predicate) {

    List<ODDefinitionSymbol>  resultList = new ArrayList<>();
    setODDefinitionSymbolsAlreadyResolved(false);
    for(String remainingSymbolName: getRemainingNameForResolveDown(name)) {
      resultList.addAll(this.resolveODDefinitionDownMany(foundSymbols, remainingSymbolName, modifier, predicate));
    }
    return resultList;
  }
}
