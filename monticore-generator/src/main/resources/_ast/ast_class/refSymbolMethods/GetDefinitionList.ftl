<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "referencedNode")}
  java.util.List<Optional<${referencedNode}>> list = new java.util.ArrayList<>();
  for (Optional<${referencedSymbol}> symbol : get${attributeName?cap_first}SymbolList()) {
    if (symbol.isPresent() && symbol.get().isPresentAstNode()) {
      list.add(Optional.ofNullable(symbol.get().getAstNode()));
    } else {
      list.add(Optional.empty());
    }
  }
  return list;
