/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.SymbolKind;

/**
 * Provides a default implementation for {@link ModelNameCalculator}.
 *
 * @author Pedram Mir Nazari
 *
 * @see ModelNameCalculator
 */
public class CommonModelNameCalculator implements ModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(String name, SymbolKind kind) {
    return ImmutableSet.of(name);
  }
}
