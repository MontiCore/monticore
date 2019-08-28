<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName", "globalScope")}
  final Collection<${symbolName}> result = new LinkedHashSet<>();

  if (checkIfContinueWithEnclosingScope(foundSymbols) && (isPresentEnclosingScope())) {
    if (!(enclosingScope.get() instanceof ${globalScope})) {
      Log.warn("0xA1039 The artifact scope " + getNameOpt().orElse("") + " should have a global scope as enclosing scope or no "
              + "enclosing scope at all.");
      }
    foundSymbols = foundSymbols | result.size() > 0;
    final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, packageName, imports);

    for (final String potentialQualifiedName : potentialQualifiedNames) {
      final Collection<${symbolName}> resolvedFromEnclosing = enclosingScope.get().resolve${simpleName}Many(foundSymbols, potentialQualifiedName, modifier, predicate);
      foundSymbols = foundSymbols | resolvedFromEnclosing.size() > 0;
      result.addAll(resolvedFromEnclosing);
    }
  }
  return result;