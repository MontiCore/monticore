${tc.signature("attributeName", "referencedSymbol", "symbolName", "referencedNode")}
  java.util.List<Optional<${referencedNode}>> list = new java.util.ArrayList<>();
  for (Optional<${referencedSymbol}> symbol : ${attributeName}Map.values()) {
    if (symbol.isPresent()) {
      list.add(symbol.get().get${symbolName}Node());
    } else {
      list.add(Optional.empty());
    }
  }
  return list;