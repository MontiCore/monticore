<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
  if (checkIfContinueWithEnclosingScope(foundSymbols) && (isPresentEnclosingScope())) {
    return getEnclosingScope().resolve${simpleName}Many(foundSymbols, name, modifier, predicate);
  }
  return Collections.emptySet();