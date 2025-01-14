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
	else {
	  String symRefName = ${symbolReferenceName}.class.getSimpleName();
	  // todo remove if-clause: https://git.rwth-aachen.de/monticore/monticore/-/issues/3998
	  String warnStr = "0xA0310${generatedError} " + symRefName
                     + ": Unable to load full information of '"
                     + name + "'. Please check your symbol table";
	  if (!symRefName.equals("ProdSymbolSurrogate") &&
	      !symRefName.equals("OOTypeSymbolSurrogate")) {
	    Log.warn(warnStr);
	  }
	  else {
	    Log.info(warnStr + " This will be a warning in the future.", symRefName);
	  }
	}
  }
  return delegate.isPresent();
