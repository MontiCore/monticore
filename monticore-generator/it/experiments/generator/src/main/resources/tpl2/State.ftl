<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to State @ grammar HierAutomata

   It demonstrates how to use a global Variable to count
-->
    state ${ast.name}   // arguments are:  ${statedepth}
    <#if ast.isInitial()> <<initial>> </#if>
    <#if ast.isFinal()  > <<final>> </#if>
    <#if ast.isPresentStateBody()>
      <#if 2<=statedepth>
        ${glex.changeGlobalVar("statedepth", statedepth+1)}
        ${tc.include("tpl2.StateBody", ast.stateBody)}
        ${glex.changeGlobalVar("statedepth", statedepth-1)}
      </#if>
    </#if>
