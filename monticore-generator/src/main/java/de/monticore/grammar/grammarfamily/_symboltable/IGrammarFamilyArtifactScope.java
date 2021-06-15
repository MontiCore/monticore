/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import java.util.ArrayList;
import java.util.List;

public interface IGrammarFamilyArtifactScope extends IGrammarFamilyArtifactScopeTOP {

  // TODO: delete the method if it is generated correctly
  @Override
  default List<String> getRemainingNameForResolveDown(String symbolName) {
    List<String> remainingSymbolNames = new ArrayList<>();
    String packageAS = this.getPackageName();
    if(symbolName.startsWith(packageAS)){
      if(!packageAS.equals("")){
        symbolName = symbolName.substring(packageAS.length()+1);
      }
      String asName = this.getName() + ".";
      remainingSymbolNames.add(symbolName);
      if(symbolName.startsWith(asName)){
        symbolName = symbolName.substring(asName.length());
        remainingSymbolNames.add(symbolName);
      }
    }

    return remainingSymbolNames;
  }

  @Override
  default public boolean checkIfContinueAsSubScope(String symbolName) {
    return true;
    /*
      always check the subscopes
      there are 2 constellations, what the symbolName could contain:
      1. an absolute name like "a.b.A":
         in this case, we traverse further in the subscopes
         (possibly with the package name removed, when this.getName() has parts of the package name)
      2. a QualifiedName without a package, like "A" or "A.name":
         search in all subscopes, for any defined type with this name
     */
  }
}
