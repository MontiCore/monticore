<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to State @ grammar HierAutomata
-->
    state ${ast.name} 
    <#if ast.isInitial()> <<initial>> </#if>
    <#if ast.isFinal()  > <<final>> </#if>
    <#if ast.isPresentStateBody()>
      ${include2("tpl3.StateBody", ast.getStateBody())}
    </#if>

