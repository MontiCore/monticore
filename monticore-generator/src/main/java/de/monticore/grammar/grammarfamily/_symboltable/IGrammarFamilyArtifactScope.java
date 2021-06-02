package de.monticore.grammar.grammarfamily._symboltable;

public interface IGrammarFamilyArtifactScope extends IGrammarFamilyArtifactScopeTOP {

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
