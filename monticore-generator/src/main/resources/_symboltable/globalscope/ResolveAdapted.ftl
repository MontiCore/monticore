<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("fullSymbolName", "resolvingDelegateInterface", "simpleSymbolName")}
List<${fullSymbolName}> adaptedSymbols = new ArrayList<${fullSymbolName}>();
for (${resolvingDelegateInterface} symDel : getAdapted${simpleSymbolName}ResolvingDelegateList()) {
adaptedSymbols.addAll(symDel.resolveAdapted${simpleSymbolName}(foundSymbols, name, modifier, predicate));
}
return adaptedSymbols;