/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import java.util.Collections;
import java.util.Set;

import de.monticore.symboltable.SymbolKind;

/**
 * @author Pedram Mir Seyed Nazari
 */
// TODO PN Doc
public final class EmptyModelNameCalculator implements ModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(String name, SymbolKind kind) {
    return Collections.emptySet();
  }
}
