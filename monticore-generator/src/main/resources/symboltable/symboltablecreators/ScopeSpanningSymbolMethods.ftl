<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol", "symbolName", "ruleName", "astName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

  ${includeArgs("symboltable.symboltablecreators.SymbolMethods", ruleSymbol, symbolName, ruleName, astName)}

  ${includeArgs("symboltable.symboltablecreators.EndVisitMethod", ruleSymbol, astName)}
