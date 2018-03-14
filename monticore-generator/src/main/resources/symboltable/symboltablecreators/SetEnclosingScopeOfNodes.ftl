<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
   <#if genHelper.isStartRule(ruleSymbol)>
   setEnclosingScopeOfNodes(ast);
   </#if>
