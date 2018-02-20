<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}

<#assign ruleName = ruleSymbol.getName()>
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign astPrefix = fqn + "._ast.AST">

  ${includeArgs("symboltable.symboltablecreators.SymbolMethods", ruleSymbol)}

  ${includeArgs("symboltable.symboltablecreators.EndVisitMethod", ruleSymbol)}
