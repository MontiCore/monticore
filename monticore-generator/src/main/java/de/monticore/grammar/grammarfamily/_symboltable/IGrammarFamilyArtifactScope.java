/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import java.util.ArrayList;
import java.util.List;

// TODO NJ: Should not be necessary any more on next CD4A version, when packages are used in its full extend
public interface IGrammarFamilyArtifactScope extends IGrammarFamilyArtifactScopeTOP {
  default public List<String> getRemainingNameForResolveDown(String symbolName) {
    List<String> remainingSymbolNames = new ArrayList<>();
      String packageAS = this.getPackageName();
      if (symbolName.startsWith(packageAS)) {
        if (!packageAS.equals("")) {
          symbolName = symbolName.substring(packageAS.length() + 1);
        }
        String asName = this.getName();

        if (symbolName.startsWith(asName) && symbolName.contains(".")) {
          symbolName = symbolName.substring(asName.length() + 1);
        }

        remainingSymbolNames.add(symbolName);
      }
    return remainingSymbolNames;
  }
}
