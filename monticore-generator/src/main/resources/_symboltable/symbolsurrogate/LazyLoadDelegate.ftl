<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName", "simpelName", "scopeName", "generatedError1", "generatedError2")}
  if(!delegate.isPresent()){
    com.google.common.base.Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(name), " 0xA4070${generatedError1} Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "${symbolName}" + ").", ${symbolReferenceName}.class.getSimpleName());
    if(!(this.enclosingScope instanceof ${scopeName})){
      Log.error("0xA4070${generatedError2} The enclosingScope needs to be a subtype of ${scopeName}.");
    }
    Optional<${symbolName}> resolvedSymbol = ((${scopeName}) enclosingScope).resolve${simpelName}(name);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
      ${symbolReferenceName}.class.getSimpleName());
      delegate = Optional.of(resolvedSymbol.get());
      return delegate.get();
    } else {
      Log.error("0xA1038 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
        name + "' (Kind " + "${symbolName}" + ").");
      return this;
    }
  }else{
    return delegate.get();
  }