<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType", "simpleName")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().get${simpleName}Node();
    } else {
      Optional<${referencedSymbolType}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().get${simpleName}Node() : Optional.empty();
    }