<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName")}
  // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
  // during the current execution of the resolution algorithm
  if (is${simpleName}SymbolsAlreadyResolved()) {
    return new ArrayList<>();
  }

  // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
  // to prevent resolving cycles caused by cyclic symbol adapters
  set${simpleName}SymbolsAlreadyResolved(true);
  final List<${symbolFullName}> resolvedSymbols = this.resolve${simpleName}LocallyMany(foundSymbols, name, modifier, predicate);
  foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
  set${simpleName}SymbolsAlreadyResolved(false);

  final String resolveCall = "resolveMany(\"" + name + "\", \"" + "${simpleName}Symbol"
  + "\") in scope \"" + (isPresentName() ? getName() : "") + "\"";
  Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");

  // (2) continue with enclosingScope, if either no symbol has been found yet or this scope is non-shadowing
  final List<${symbolFullName}> resolvedFromEnclosing = continue${simpleName}WithEnclosingScope(foundSymbols, name, modifier, predicate);

  // (3) unify results
  resolvedSymbols.addAll(resolvedFromEnclosing);
  Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");

  return resolvedSymbols;