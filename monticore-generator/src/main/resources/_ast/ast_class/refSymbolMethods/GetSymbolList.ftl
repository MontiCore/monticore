<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "simpleSymbolName")}
  update${attributeName?cap_first}();
  java.util.List<Optional<${referencedSymbol}>> temp = new java.util.ArrayList<>();
  for (String element : this.${simpleSymbolName}) {
    if (${attributeName}.containsKey(element)) {
      temp.add(Optional.of(${attributeName}.get(element)));
    } else {
      temp.add(Optional.empty());
    }
  }
  return temp;

