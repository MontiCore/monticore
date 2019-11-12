<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().getAstNodeOpt();
    } else {
      Optional<${referencedSymbolType}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().getAstNodeOpt() : Optional.empty();
    }