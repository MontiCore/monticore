<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName", "simpelName")}
  if(!delegate.isPresent()){
    com.google.common.base.Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(name), " 0xA4070 Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "${symbolName}" + ").", ${symbolReferenceName}.class.getSimpleName());
    Optional<${symbolName}> resolvedSymbol = enclosingScope.resolve${simpelName}(name);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
      ${symbolReferenceName}.class.getSimpleName());
      delegate = Optional.of(resolvedSymbol.get());
    } else {
      Log.error("0xA1038 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
        name + "' (Kind " + "${symbolName}" + ").");
    }
    return resolvedSymbol.get();
  }else{
    return delegate.get();
  }