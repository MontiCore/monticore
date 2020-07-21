<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateBody @ grammar HierAutomata
-->
  /*StateBody*/
  {
    <#list ast.getStatesList() as s>
        ${tc.include("tpl.State", s)};
    </#list>
    <#list ast.getTransitionsList() as t>
        ${tc.include("tpl.Transition", t)};
    </#list>
  }/*end*/
