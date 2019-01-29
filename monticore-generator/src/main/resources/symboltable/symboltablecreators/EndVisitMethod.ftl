<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol", "astName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

  @Override
  public void endVisit(${astName} ast) {
    removeCurrentScope();
    <#if genHelper.isStartRule(ruleSymbol)>
    setEnclosingScopeOfNodes(ast);
    </#if>
  }
