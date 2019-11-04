<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName", "simpelName")}
  com.google.common.base.Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(name), " 0xA4070 Symbol name may not be null or empty.");

  Log.debug("Load full information of '" + name + "' (Kind " + "${symbolName}" + ").", ${symbolReferenceName}.class.getSimpleName());
  isAlreadyLoaded = true;
  Optional<${symbolName}> resolvedSymbol = enclosingScope.resolve${simpelName}(name);

  if (resolvedSymbol.isPresent()) {
    Log.debug("Loaded full information of '" + name + "' successfully.",
    ${symbolReferenceName}.class.getSimpleName());
  } else {
    Log.debug("Cannot load full information of '" + name, ${symbolReferenceName}.class.getSimpleName());
  }
  return resolvedSymbol;