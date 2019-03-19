${tc.signature("attributeName", "referencedSymbol", "simpleName", "referencedNode")}
  java.util.List<${referencedNode}> list = new java.util.ArrayList<>();
  for (Optional<${referencedSymbol}> symbol : get${attributeName?cap_first}SymbolList()) {
    if (symbol.isPresent()) {
      list.add(symbol.get().get${simpleName}Node());
    } else {
      list.add(Optional.empty());
    }
  }
  return list;