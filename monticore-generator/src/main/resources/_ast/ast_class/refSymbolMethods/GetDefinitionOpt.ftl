<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().getAstNode();
    } else {
      Optional<${referencedSymbolType}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().getAstNode() : Optional.empty();
    }