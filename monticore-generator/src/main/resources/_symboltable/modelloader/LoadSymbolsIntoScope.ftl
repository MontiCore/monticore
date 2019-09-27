<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "scopeInterface", "scopeDeSer")}
  final de.monticore.io.paths.ModelCoordinate resolvedCoordinate = resolveSymbol(qualifiedModelName, modelPath);
  if (resolvedCoordinate.hasLocation()) {
    Optional<${scopeInterface}> deser  = new ${scopeDeSer}().load(resolvedCoordinate.getLocation());
    if(deser.isPresent()) {
      enclosingScope.addSubScope(deser.get());
      return true;
    }
  }
  return false;