<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName", "simpelName", "scopeName", "generatedError")}
  if(!delegate.isPresent()){
    Log.debug("Load full information of '" + name + "' (Kind " + "${symbolName}" + ").", ${symbolReferenceName}.class.getSimpleName());
    if(!(this.enclosingScope instanceof ${scopeName})){
      Log.error("0xA4071${generatedError} The enclosingScope needs to be a subtype of ${scopeName}.");
    }
    Optional<${symbolName}> resolvedSymbol = ((${scopeName}) enclosingScope).resolve${simpelName}(name);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
      ${symbolReferenceName}.class.getSimpleName());
      delegate = Optional.of(resolvedSymbol.get());
    }
  }
  return delegate.isPresent();
