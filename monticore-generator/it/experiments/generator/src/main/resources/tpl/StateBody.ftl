<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateBody @ grammar HierAutomaton
-->
  /*StateBody*/
  {
    <#list ast.stateList as s>
        ${tc.include("tpl.State", s)};
    </#list>
    <#list ast.transitionList as t>
        ${tc.include("tpl.Transition", t)};
    </#list>
  }/*end*/
