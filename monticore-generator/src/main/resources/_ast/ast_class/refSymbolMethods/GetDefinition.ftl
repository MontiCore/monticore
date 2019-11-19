<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "attributeName", "referencedSymbolType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    if (${attributeName}Symbol.isPresent() && ${attributeName}Symbol.get().isPresentAstNode()) {
      return ${attributeName}Symbol.get().getAstNode();
    } else if (isPresent${attributeName?cap_first}Symbol()) {
      ${referencedSymbolType} symbol = get${attributeName?cap_first}Symbol();
      if (symbol.isPresentAstNode()) {
        return symbol.getAstNode();
      }
    }
    Log.error("0xA7003${genHelper.getGeneratedErrorCode(ast)} ${attributeName}Definition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();