<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "referencedSymbol", "symbolName")}
    if (${attribute.getName()}Symbol.isPresent()) {
      return ${attribute.getName()}Symbol.get().get${symbolName}Node();
    } else {
      Optional<${referencedSymbol}> symbol = get${attribute.getName()?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().get${symbolName}Node() : Optional.empty();
    }