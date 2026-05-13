<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolReferenceName", "symbolName", "simpleName", "scopeName", "generatedErrorEmpty", "generatedErrorScopeType")}
    if (name == null || name.isEmpty()) throw new IllegalArgumentException("0xA4072${generatedErrorEmpty} Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "${symbolName}" + ").", ${symbolReferenceName}.class.getSimpleName());
    if(!(this.enclosingScope instanceof ${scopeName})){
      Log.error("0xA4073${generatedErrorScopeType} The enclosingScope needs to be a subtype of ${scopeName}.");
			return Optional.empty();
    }
    Optional<${symbolName}> resolvedSymbol = ((${scopeName}) enclosingScope).resolve${simpleName}(name);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
      ${symbolReferenceName}.class.getSimpleName());
    } else {
      Log.error("0xA1037 " + ${symbolReferenceName}.class.getSimpleName() + " Could not load full information of '" +
        name + "' (Kind " + "${symbolName}" + ").");
    }
		return resolvedSymbol;