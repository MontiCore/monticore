<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to State @ grammar HierAutomaton
-->
    state ${ast.name} 
    <#if ast.isInitial()> <<initial>> </#if>
    <#if ast.isFinal()  > <<final>> </#if>
    <#if ast.isPresentStateBody()>
      ${tc.include("tpl.StateBody", ast.getStateBody())}
    </#if>

