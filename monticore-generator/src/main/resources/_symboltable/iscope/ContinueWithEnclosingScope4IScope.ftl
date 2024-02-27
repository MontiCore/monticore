<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope() != null)) {
    return getEnclosingScope().resolve${simpleName}Many(foundSymbols, name, modifier, predicate);
  }
  return new de.monticore.symboltable.SetAsListAdapter<>();