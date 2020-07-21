<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolNameList")}
  List<de.monticore.symboltable.ISymbol> topSymbolList = new ArrayList<>();
  <#list symbolNameList as symbolName>
    topSymbolList.addAll(get${symbolName}Symbols().values());
  </#list>
  if (topSymbolList.size() == 1) {
    return Optional.of(topSymbolList.get(0));
  }
  // there is no top level symbol, if more than one symbol exists.
  return Optional.empty();