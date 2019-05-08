<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbol")}
    if (${attributeName}Symbol.isPresent()) {
      return ${attributeName}Symbol.get().getAstNode();
    } else {
      Optional<${referencedSymbol}> symbol = get${attributeName?cap_first}SymbolOpt();
      return (symbol.isPresent()) ? symbol.get().getAstNode() : Optional.empty();
    }