/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar;

public class MCGrammarSymbolTableHelperFix {

  public static void cleanUp() {
    // Can be removed after 7.6.0-SNAPSHOT
    MCGrammarSymbolTableHelper.superProdCache.invalidateAll();
  }

}
