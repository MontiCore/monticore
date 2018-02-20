<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}

<#assign ruleName = ruleSymbol.getName()>
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>
<#assign astPrefix = fqn + "._ast.AST">

  @Override
  public void endVisit(${astPrefix}${ruleName} ast) {
    removeCurrentScope();
    <#if genHelper.isStartRule(ruleSymbol)>
    setEnclosingScopeOfNodes(ast);
    </#if>
  }
