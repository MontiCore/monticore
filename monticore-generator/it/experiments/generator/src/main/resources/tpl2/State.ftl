<#--
   Template, belongs to State @ grammar HierAutomaton
-->
    state ${ast.name}   // arguments are:  ${statedepth}
    <#if ast.isInitial()> <<initial>> </#if>
    <#if ast.isFinal()  > <<final>> </#if>
    <#if ast.isPresentStateBody()>
      <#if 2<=statedepth>
        ${glex.changeGlobalVar("statedepth", statedepth+1)}
        ${tc.include("tpl2.StateBody", ast.stateBody)}
<#-- XXX TODO BUG MB:
das statement sollte notwendig sein (ist ja eine globale Variable):
          	${glex.changeGlobalVar("statedepth", statedepth-1)}
Die Variable hat sich aber lokal nicht verändert und
deshalb ist nachfolgende Zeile notwendig 
(obwohl sie zunächst sinnlos erscheinen mag)
        ${glex.changeGlobalVar("statedepth", statedepth)}
(am besten sichtbar, wenn man out/3pingPong.aut verfolgt)
-->
      </#if>
    </#if>
