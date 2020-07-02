<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName", "simpleName", "globalScope")}
  final List<${symbolName}> result = new ArrayList<>();

if (checkIfContinueWithEnclosingScope(foundSymbols) && getEnclosingScope() != null) {
if (!(getEnclosingScope() instanceof ${globalScope})) {
      Log.warn("0xA1039 The artifact scope " + (isPresentName() ? getName() : "") + " should have a global scope as enclosing scope or no "
              + "enclosing scope at all.");
      }
    foundSymbols = foundSymbols | result.size() > 0;
    final Set<String> potentialQualifiedNames = calculateQualifiedNames(name, getPackageName(), getImportList());

    for (final String potentialQualifiedName : potentialQualifiedNames) {
  final List<${symbolName}> resolvedFromEnclosing = getEnclosingScope().resolve${simpleName}Many(foundSymbols,
  potentialQualifiedName, modifier, predicate);
      foundSymbols = foundSymbols | resolvedFromEnclosing.size() > 0;
      result.addAll(resolvedFromEnclosing);
    }
  }
  return result;