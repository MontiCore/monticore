<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("fullSymbolName", "resolvingDelegateInterface", "simpleSymbolName")}
    List<${fullSymbolName}> adaptedSymbols = new de.monticore.symboltable.SetAsListAdapter<>();
    for (${resolvingDelegateInterface} symDel : getAdapted${simpleSymbolName}ResolverList()) {
        adaptedSymbols.addAll(symDel.resolveAdapted${simpleSymbolName}(foundSymbols, name, modifier, predicate));
    }
    return adaptedSymbols;