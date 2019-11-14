<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeName", "referencedSymbolType")}
    if (${attributeName}Symbol.isPresent() && ${attributeName}Symbol.get().isPresentAstNode()) {
      return true;
    } else if (isPresent${attributeName?cap_first}Symbol()) {
      ${referencedSymbolType} symbol = get${attributeName?cap_first}Symbol();
      if (symbol.isPresentAstNode()) {
        return true;
      }
    }
    return false;