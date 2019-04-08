/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

/**
 * Provides useful information of the current resolution process, e.g., the
 * scope that started the process.
 *
 *
 */
public class SymbolsFoundInfo {

  private boolean areSymbolsFound = false;


  public boolean areSymbolsFound() {
    return areSymbolsFound;
  }

  public void updateSymbolsFound(boolean areSymbolsFound) {
    this.areSymbolsFound = this.areSymbolsFound || areSymbolsFound;
  }
}
