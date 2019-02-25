<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType", "simpleSymbolName")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().get${simpleSymbolName}Node();
    } else {
      Optional<${referencedSymbolType}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().get${simpleSymbolName}Node() : Optional.empty();
    }