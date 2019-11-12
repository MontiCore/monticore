<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolList", "scopeInterface")}
<#list symbolList as symbol>
  if (!node.getLocal${symbol}Symbols().isEmpty()) {
    printer.beginArray("${symbol?uncap_first}Symbols");
    node.getLocal${symbol}Symbols().stream().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }
</#list>
  List<${scopeInterface}> subScopes = filterRelevantSubScopes(node.getSubScopes());
  if (!subScopes.isEmpty()) {
    printer.beginArray(de.monticore.symboltable.serialization.JsonConstants.SUBSCOPES);
    subScopes.stream().forEach(s -> s.accept(getRealThis()));
    printer.endArray();
  }
