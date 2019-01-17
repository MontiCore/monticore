<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol", "symbolName")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().get${symbolName}Node();
    } else {
      Optional<${referencedSymbol}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().get${symbolName}Node() : Optional.empty();
    }