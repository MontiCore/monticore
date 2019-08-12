${tc.signature("attributeName", "referencedSymbol", "referencedNode")}
  java.util.List<Optional<${referencedNode}>> list = new java.util.ArrayList<>();
  for (Optional<${referencedSymbol}> symbol : get${attributeName?cap_first}SymbolList()) {
    if (symbol.isPresent()) {
      list.add(symbol.get().getAstNode());
    } else {
      list.add(Optional.empty());
    }
  }
  return list;
