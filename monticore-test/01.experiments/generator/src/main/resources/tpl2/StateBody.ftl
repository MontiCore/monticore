<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateBody @ grammar HierAutomata
-->
  /*StateBody*/
  {
    <#list ast.getStateList() as s>
        ${tc.include("tpl2.State", s)};
    </#list>
    <#list ast.getTransitionList() as t>
        ${tc.include("tpl2.Transition", t)};
    </#list>
  }/*end*/
