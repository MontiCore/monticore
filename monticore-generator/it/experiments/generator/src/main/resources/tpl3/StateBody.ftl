<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateBody @ grammar HierAutomata
-->
  /*StateBody*/
  {
    <#list ast.getStateList() as s>
        ${include2("tpl3.State", s)};
    </#list>
    <#list ast.getTransitionList() as t>
        ${include2("tpl3.Transition", t)};
    </#list>
  }/*end*/
