<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "scopeInterface", "scopeDeSer")}
  final de.monticore.io.paths.ModelCoordinate resolvedCoordinate = resolveSymbol(qualifiedModelName, modelPath);
  if (resolvedCoordinate.hasLocation()) {
    ${scopeInterface} deser  = new ${scopeDeSer}().load(resolvedCoordinate.getLocation(), enclosingScope);
    enclosingScope.addSubScope(deser);
    return true;
  }
  return false;