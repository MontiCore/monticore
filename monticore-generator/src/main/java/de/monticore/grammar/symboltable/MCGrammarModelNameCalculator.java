/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.symboltable;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.monticore.CommonModelNameCalculator;
import de.monticore.symboltable.SymbolKind;

public class MCGrammarModelNameCalculator extends CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    String modelName = null;

    if (MCGrammarSymbol.KIND.isKindOf(kind)) {
      // e.g., if p.X, return unchanged
      modelName = name;
    }
    // TODO MB: Muss man für die anderen Symbole was tun?
    if (modelName != null) {
      return ImmutableSet.of(modelName);
    }

    return Collections.emptySet();
  }
}
