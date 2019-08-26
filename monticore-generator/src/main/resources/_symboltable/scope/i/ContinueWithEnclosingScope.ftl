<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
  if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
    return getEnclosingScope().get().resolve${simpleName}Many(foundSymbols, name, modifier, predicate);
  }
  return Collections.emptySet();