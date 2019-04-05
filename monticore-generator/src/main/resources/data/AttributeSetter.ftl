<#-- (c) https://github.com/MontiCore/monticore -->
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if genHelper.isListType(ast.printType())>
  set${ast.getName()?cap_first}List(${ast.getName()});
<#elseif genHelper.isOptional(ast.getType())>
  set${ast.getName()?cap_first}Opt(${ast.getName()});
<#else>
  set${ast.getName()?cap_first}(${ast.getName()});
</#if>