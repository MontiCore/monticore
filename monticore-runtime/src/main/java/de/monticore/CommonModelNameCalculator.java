/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.SymbolKind;

import java.util.Set;

/**
 * Provides a default implementation for {@link ModelNameCalculator}.
 *
 *
 * @see ModelNameCalculator
 */
@Deprecated
public class CommonModelNameCalculator implements ModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(String name, SymbolKind kind) {
    return ImmutableSet.of(name);
  }
}
